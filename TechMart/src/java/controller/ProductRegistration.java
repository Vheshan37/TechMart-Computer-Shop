package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.DefaultValidator;
import model.HibernateUtil;
import model.dto.Response_DTO;
import model.dto.User_DTO;
import model.entity.Brand;
import model.entity.Category;
import model.entity.CategoryHasBrand;
import model.entity.Color;
import model.entity.District;
import model.entity.Feature;
import model.entity.Product;
import model.entity.ProductHasFeatureList;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "ProductRegistration", urlPatterns = {"/ProductRegistration"})
public class ProductRegistration extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        String category = req.getParameter("category");
        String brand = req.getParameter("brand");
        String color = req.getParameter("color");
        String title = req.getParameter("title");
        String qty = req.getParameter("qty");
        String price = req.getParameter("price");
        String deliveryCity = req.getParameter("deliveryCity");
        String deliveryIn = req.getParameter("deliveryIn");
        String deliveryOut = req.getParameter("deliveryOut");

        JsonArray featureList = gson.fromJson(req.getParameter("featureList"), JsonArray.class);
        Collection<Part> images = req.getParts().stream()
                .filter(part -> "images".equals(part.getName()))
                .collect(Collectors.toList());

        if (category.isEmpty()) {
            responseObject.setContent("Select a valid Category.");
        } else if (brand.isEmpty()) {
            responseObject.setContent("Select a valid Brand.");
        } else if (color.isEmpty()) {
            responseObject.setContent("Select a valid Color.");
        } else if (title.isEmpty()) {
            responseObject.setContent("Enter the product Title.");
        } else if (qty.isEmpty()) {
            responseObject.setContent("Enter the product Quantity.");
        } else if (!DefaultValidator.isInteger(qty) || Integer.parseInt(qty) <= 0) {
            responseObject.setContent("Enter a valid Quantity.");
        } else if (price.isEmpty()) {
            responseObject.setContent("Enter the product Price.");
        } else if (!DefaultValidator.isDouble(price)) {
            responseObject.setContent("Enter a valid Price.");
        } else if (deliveryCity.isEmpty()) {
            responseObject.setContent("Select a Delivery district.");
        } else if (deliveryIn.isEmpty()) {
            responseObject.setContent("Enter the Delivery (In) price.");
        } else if (!DefaultValidator.isDouble(deliveryIn)) {
            responseObject.setContent("Enter a valid Delivery (In) price.");
        } else if (deliveryOut.isEmpty()) {
            responseObject.setContent("Enter the Delivery (Out) price.");
        } else if (!DefaultValidator.isDouble(deliveryOut)) {
            responseObject.setContent("Enter a valid Delivery (Out) price.");
        } else if (featureList.size() == 0) {
            responseObject.setContent("Include at least one product feature.");
        } else if (images.isEmpty()) {
            responseObject.setContent("Include at least one product image.");
        } else {

            Criteria categoryHasBrandCriteria = session.createCriteria(CategoryHasBrand.class);
            categoryHasBrandCriteria.add(
                    Restrictions.and(
                            Restrictions.eq("category", session.load(Category.class, Integer.valueOf(category))),
                            Restrictions.eq("brand", session.load(Brand.class, Integer.valueOf(brand)))
                    )
            );

            int categoryHasBrandID;

            if (!categoryHasBrandCriteria.list().isEmpty()) { // Get CategoryHasBrand ID 
                CategoryHasBrand categoryHasBrand = (CategoryHasBrand) categoryHasBrandCriteria.list().get(0);
                categoryHasBrandID = categoryHasBrand.getId();
            } else { // Add new CategoryHasBrand
                CategoryHasBrand categoryHasBrand = new CategoryHasBrand();
                categoryHasBrand.setBrand((Brand) session.load(Brand.class, Integer.valueOf(brand)));
                categoryHasBrand.setCategory((Category) session.load(Category.class, Integer.valueOf(category)));
                session.save(categoryHasBrand);
                categoryHasBrandID = categoryHasBrand.getId();
            }

            User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
            int userId = user.getId();

            Product product = new Product();
            product.setTitle(title);
            product.setPrice(Double.parseDouble(price));
            product.setQuantity(Integer.parseInt(qty));
            product.setDeliveryIn(Double.parseDouble(deliveryIn));
            product.setDeliveryOut(Double.parseDouble(deliveryOut));
            product.setColor((Color) session.load(Color.class, Integer.valueOf(color)));
            product.setCategoryHasBrand((CategoryHasBrand) session.load(CategoryHasBrand.class, Integer.valueOf(categoryHasBrandID)));
            product.setDistrict((District) session.load(District.class, Integer.valueOf(deliveryCity)));
            product.setUser((User) session.load(User.class, userId));
            product.setDateTime(new Date());

            session.save(product);
            int productID = product.getId();

            for (JsonElement jsonElement : featureList) {
                JsonObject featureObject = jsonElement.getAsJsonObject();
                String FeatureId = featureObject.get("id").getAsString();
                String value = featureObject.get("value").getAsString();

                ProductHasFeatureList productHasFeatureList = new ProductHasFeatureList();
                productHasFeatureList.setFeature((Feature) session.load(Feature.class, Integer.valueOf(FeatureId)));
                productHasFeatureList.setProduct((Product) session.load(Product.class, Integer.valueOf(productID)));
                productHasFeatureList.setValue(value);

                session.save(productHasFeatureList);
            }

            int imageCount = 1;

            for (Part image : images) {
                String fileName = image.getSubmittedFileName();

                String fileExtension = "";
                if (fileName != null && fileName.lastIndexOf('.') > 0) {
                    fileExtension = fileName.substring(fileName.lastIndexOf('.'));
                }

                InputStream fileStream = image.getInputStream();
                String applicationPath = req.getServletContext().getRealPath("//img//product").replace("\\build\\web", "\\web");
                String filePath = applicationPath + "//" + title + "_(" + productID + ")_(" + imageCount + ").jpg";
                File newFile = new File(filePath);

                if (!fileExtension.isEmpty()) {
                    Files.copy(
                            fileStream,
                            newFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                    imageCount++;
                }
            }

            session.beginTransaction().commit();

            responseObject.setSuccess(true);
            responseObject.setContent("Product Registration competed");
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
