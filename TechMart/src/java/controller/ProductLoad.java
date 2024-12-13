package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.User_DTO;
import model.entity.Address;
import model.entity.CategoryHasBrand;
import model.entity.Product;
import model.entity.ProductHasFeatureList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "ProductLoad", urlPatterns = {"/ProductLoad"})
public class ProductLoad extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String productId = req.getParameter("id");

        Product product = (Product) session.get(Product.class, Integer.valueOf(productId));
        String title = product.getTitle();
        double price = product.getPrice();
        int stock = product.getQuantity();
        int productID = product.getId();
        String description = null;
        List<String> validFilePaths = new ArrayList<>();
        List<JsonObject> featureListArray = new ArrayList<>();

        Criteria productHasFeatureTable = session.createCriteria(ProductHasFeatureList.class);
        productHasFeatureTable.add(Restrictions.eq("product", product));
        List<ProductHasFeatureList> productHasFeatureList = productHasFeatureTable.list();

        for (ProductHasFeatureList productHasFeatureItem : productHasFeatureList) {

            JsonObject itemObject = new JsonObject();
            itemObject.add("feature", gson.toJsonTree(productHasFeatureItem.getFeature().getFeature()));
            itemObject.add("value", gson.toJsonTree(productHasFeatureItem.getValue()));

            featureListArray.add(itemObject);

            if ("Description".equals(productHasFeatureItem.getFeature().getFeature())) {
                description = productHasFeatureItem.getValue();
            }
        }

        for (int i = 1; i <= 5; i++) {
            String applicationPath = req.getServletContext().getRealPath("//img//product").replace("\\build\\web", "\\web");
            String filePath = applicationPath + "//" + title + "_(" + productID + ")_(" + i + ").jpg";

            File file = new File(filePath);

            if (file.exists()) {
                String imagePath = title + "_(" + productID + ")_(" + i + ").jpg";
                validFilePaths.add(imagePath);
            } else {
                break;
            }
        }

        Criteria similarProductTable = session.createCriteria(Product.class);
        similarProductTable.add(Restrictions.ne("id", product.getId()));
        similarProductTable.add(Restrictions.eq("categoryHasBrand", session.get(CategoryHasBrand.class, product.getCategoryHasBrand().getId())));
        similarProductTable.setMaxResults(8);
        List<Product> similarProductList = similarProductTable.list();

        JsonObject responseObject = new JsonObject();
        responseObject.add("id", gson.toJsonTree(productID));
        responseObject.add("title", gson.toJsonTree(title));
        responseObject.add("price", gson.toJsonTree(price));
        responseObject.add("stock", gson.toJsonTree(stock));
        responseObject.add("description", gson.toJsonTree(description));
        responseObject.add("featureList", gson.toJsonTree(featureListArray));
        responseObject.add("imageList", gson.toJsonTree(validFilePaths));
        responseObject.add("similarProductList", gson.toJsonTree(similarProductList));

        responseObject.addProperty("login", false);
        responseObject.addProperty("login_status", "Invalid user");
        if (req.getSession().getAttribute("tm_user") != null) {

            User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
            int userId = user.getId();

            Criteria userAddressTable = session.createCriteria(Address.class);
            userAddressTable.add(Restrictions.eq("user", user));

            
            if (!userAddressTable.list().isEmpty()) {
                responseObject.addProperty("login", true);

                Address userAddress = (Address) userAddressTable.uniqueResult();

                if (product.getDistrict().getId() == userAddress.getCity().getDistrict().getId()) {
                    responseObject.addProperty("delivery_cost", product.getDeliveryIn());
                } else {
                    responseObject.addProperty("delivery_cost", product.getDeliveryOut());
                }

                JsonObject payhereData = new JsonObject();
                payhereData.addProperty("merchant_id", 1221196);
                payhereData.addProperty("return_url", "index.html");
                payhereData.addProperty("cancel_url", "index.html");
                payhereData.addProperty("notify_url", "index.html");
                payhereData.addProperty("first_name", user.getFirst_name());
                payhereData.addProperty("last_name", user.getLast_name());
                payhereData.addProperty("email", user.getEmail());
                payhereData.addProperty("phone", user.getMobile());
                payhereData.addProperty("address", userAddress.getLine1() + ", " + userAddress.getLine2() + ", " + userAddress.getCity().getCity() + ". [" + userAddress.getCity().getDistrict().getDistrict() + "]");
                payhereData.addProperty("city", userAddress.getCity().getCity());
                payhereData.addProperty("country", "Sri Lanka");
                payhereData.addProperty("order_id", "");
                payhereData.addProperty("items", title);
                payhereData.addProperty("currency", "LKR");
                payhereData.addProperty("amount", price);
            }else{
                responseObject.addProperty("login_status", "Incomplete profile details");
            }
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
