package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String productId = req.getParameter("id");
        Product product = (Product) session.get(Product.class, Integer.valueOf(productId));
        String title = product.getTitle();
        double price = product.getPrice();
        int stock = product.getQuantity();

        Criteria productHasFeatureTable = session.createCriteria(ProductHasFeatureList.class);
        productHasFeatureTable.add(Restrictions.eq("product", product));
        List<ProductHasFeatureList> productHasFeatureList = productHasFeatureTable.list();
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("product", gson.toJsonTree(product));
        jsonObject.add("featureList", gson.toJsonTree(productHasFeatureList));

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

}
