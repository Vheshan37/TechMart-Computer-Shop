package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.entity.Product;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadShopView", urlPatterns = {"/LoadShopView"})
public class LoadShopView extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        JsonObject responseObject = new JsonObject();

        String queryText = req.getParameter("q");
        if (queryText.isEmpty()) {
            Criteria productsTable = session.createCriteria(Product.class);
            productsTable.addOrder(Order.desc("dateTime"));
            List<Product> ProductList = productsTable.list();

            responseObject.add("ProductList", gson.toJsonTree(ProductList));
        } else {
            Criteria productsTable = session.createCriteria(Product.class);
            productsTable.add(Restrictions.like("title", queryText, MatchMode.ANYWHERE));
            productsTable.addOrder(Order.desc("dateTime"));
            List<Product> ProductList = productsTable.list();

            responseObject.add("ProductList", gson.toJsonTree(ProductList));
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
