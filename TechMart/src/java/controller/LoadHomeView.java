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
import model.entity.Product;
import model.entity.ProductHasFeatureList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadHomeView", urlPatterns = {"/LoadHomeView"})
public class LoadHomeView extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        JsonObject responseObject = new JsonObject();
        List<String> validFilePaths = new ArrayList<>();

        Criteria productTable = session.createCriteria(Product.class);
        productTable.addOrder(Order.desc("dateTime"));
        productTable.setMaxResults(1);

        if (!productTable.list().isEmpty()) {
            Product latestProduct = (Product) productTable.list().get(0);

            Criteria productsTable = session.createCriteria(Product.class);
            productsTable.addOrder(Order.desc("dateTime"));
            productsTable.setMaxResults(8);
            List<Product> ProductList = productsTable.list();

            Criteria productHasFeatureTable = session.createCriteria(ProductHasFeatureList.class);
            productHasFeatureTable.add(Restrictions.eq("product", latestProduct));
            productHasFeatureTable.setMaxResults(8);
            List<ProductHasFeatureList> productHasFeatureList = productHasFeatureTable.list();

            for (int i = 1; i <= 5; i++) {
                String applicationPath = req.getServletContext().getRealPath("//img//product").replace("\\build\\web", "\\web");
                String filePath = applicationPath + "//" + latestProduct.getTitle() + "_(" + latestProduct.getId() + ")_(" + i + ").jpg";

                File file = new File(filePath);

                if (file.exists()) {
                    String imagePath = latestProduct.getTitle() + "_(" + latestProduct.getId() + ")_(" + i + ").jpg";
                    validFilePaths.add(imagePath);
                } else {
                    break;
                }
            }
            responseObject.add("latestProduct", gson.toJsonTree(latestProduct));
            responseObject.add("latestFeatureList", gson.toJsonTree(productHasFeatureList));
            responseObject.add("ProductList", gson.toJsonTree(ProductList));
            responseObject.add("imageList", gson.toJsonTree(validFilePaths));
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
