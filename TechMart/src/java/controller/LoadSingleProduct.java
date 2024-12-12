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
import model.dto.User_DTO;
import model.entity.Address;
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

        if (req.getSession().getAttribute("tm_user") == null) {
            jsonObject.addProperty("login", false);
        } else {

            User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
            int userId = user.getId();

            Criteria userAddressTable = session.createCriteria(Address.class);
            userAddressTable.add(Restrictions.eq("user", user));

            if (!userAddressTable.list().isEmpty()) {
                jsonObject.addProperty("login", true);

                Address userAddress = (Address) userAddressTable.uniqueResult();

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
            } else {
                jsonObject.addProperty("login", false);
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(jsonObject));
    }

}
