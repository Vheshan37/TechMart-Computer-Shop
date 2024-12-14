package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.User_DTO;
import model.entity.Address;
import model.entity.Product;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "BuyNow", urlPatterns = {"/BuyNow"})
public class BuyNow extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String productId = req.getParameter("id");
        Product product = (Product) session.get(Product.class, Integer.valueOf(productId));

        responseObject.addProperty("login", false);
        if (req.getSession().getAttribute("tm_user") != null) {

            User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
            int userId = user.getId();
            User db_user = (User) session.get(User.class, userId);

            Criteria userAddressTable = session.createCriteria(Address.class);
            userAddressTable.add(Restrictions.eq("user", db_user));

            if (!userAddressTable.list().isEmpty()) {
                responseObject.addProperty("login", true);

                Address userAddress = (Address) userAddressTable.uniqueResult();

                double product_cost = product.getPrice();

                if (product.getDistrict().getId() == userAddress.getCity().getDistrict().getId()) {
                    responseObject.addProperty("delivery_cost", product.getDeliveryIn());
                    product_cost += product.getDeliveryIn();
                } else {
                    responseObject.addProperty("delivery_cost", product.getDeliveryOut());
                    product_cost += product.getDeliveryOut();
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
                payhereData.addProperty("items", product.getTitle());
                payhereData.addProperty("currency", "LKR");
                payhereData.addProperty("amount", product_cost);

                responseObject.add("payhere_data", payhereData);
            } else {
                responseObject.addProperty("login_status", "Incomplete profile details");
            }
        } else {
            responseObject.addProperty("login_status", "Invalid user");
        }
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
