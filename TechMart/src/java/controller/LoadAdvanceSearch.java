package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.entity.Category;
import model.entity.CategoryHasBrand;
import model.entity.Product;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadAdvanceSearch", urlPatterns = {"/LoadAdvanceSearch"})
@MultipartConfig
public class LoadAdvanceSearch extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String categoryID = req.getParameter("category");
        String minPrice = req.getParameter("minPrice");
        String maxPrice = req.getParameter("maxPrice");
//        String categoryID = "2";
//        String minPrice = "1000";
//        String maxPrice = "2000";

        Criteria productsTable = session.createCriteria(Product.class);
        productsTable.addOrder(Order.desc("dateTime"));

        if (!categoryID.isEmpty()) {
            System.out.println("category found");
            Category category = (Category) session.get(Category.class, Integer.parseInt(categoryID)); // get the category with CategoryID
            Criteria categoryHasBrandTable = session.createCriteria(CategoryHasBrand.class); // get the categoryHasBrand  with Category
            categoryHasBrandTable.add(Restrictions.eq("category", category));
            List<CategoryHasBrand> categoryHasBrandList = categoryHasBrandTable.list();

            if (!minPrice.isEmpty()) {
                if (!maxPrice.isEmpty()) {
                    System.out.println("with category, min price and max price");
                    // with category, min price and max price
                    productsTable.add(Restrictions.in("categoryHasBrand", categoryHasBrandList));
                    productsTable.add(Restrictions.ge("price", Double.parseDouble(minPrice)));
                    productsTable.add(Restrictions.le("price", Double.parseDouble(maxPrice)));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                } else {
                    System.out.println("with category and min price");
                    // with category and min price
                    productsTable.add(Restrictions.in("categoryHasBrand", categoryHasBrandList));
                    productsTable.add(Restrictions.ge("price", Double.parseDouble(minPrice)));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                }
            } else {
                if (!maxPrice.isEmpty()) {
                    System.out.println("with category and max price");
                    // with category and max price
                    productsTable.add(Restrictions.in("categoryHasBrand", categoryHasBrandList));
                    productsTable.add(Restrictions.le("price", Double.parseDouble(maxPrice)));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                } else {
                    System.out.println("with category");
                    // with category
                    productsTable.add(Restrictions.in("categoryHasBrand", categoryHasBrandList));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                }
            }
        } else {
            if (!minPrice.isEmpty()) {
                if (!maxPrice.isEmpty()) {
                    System.out.println("with min price and max price");
                    // with min price and max price
                    productsTable.add(Restrictions.ge("price", Double.parseDouble(minPrice)));
                    productsTable.add(Restrictions.le("price", Double.parseDouble(maxPrice)));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                } else {
                    System.out.println("with min price");
                    // with min price only
                    productsTable.add(Restrictions.ge("price", Double.parseDouble(minPrice)));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                }
            } else {
                if (!maxPrice.isEmpty()) {
                    System.out.println("with max price");
                    // with max price only
                    productsTable.add(Restrictions.le("price", Double.parseDouble(maxPrice)));
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                } else {
                    System.out.println("without parameters");
                    // without category, min price, or max price
                    List<Product> ProductList = productsTable.list();
                    responseObject.add("ProductList", gson.toJsonTree(ProductList));
                }
            }
        }

        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
