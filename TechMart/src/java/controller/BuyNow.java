package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.PayHere;
import model.dto.User_DTO;
import model.entity.Address;
import model.entity.Order;
import model.entity.OrderStatus;
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
            responseObject.add("user", gson.toJsonTree(db_user));

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

                // get order status
                Criteria orderStatusTable = session.createCriteria(OrderStatus.class);
                orderStatusTable.add(Restrictions.eq("status", "pending"));
                OrderStatus orderStatus = (OrderStatus) orderStatusTable.uniqueResult();

                // save order
                Order order = new Order();
                order.setDateTime(new Date());
                order.setUser(db_user);
                order.setStatus(orderStatus);
                int orderId = (int) session.save(order);
                responseObject.addProperty("orderID", orderId);

                String formatedAmount = new DecimalFormat("0.00").format(product_cost);
                String orderIDString = String.valueOf(orderId);
                String currency = "LKR";
                String merchantId = "1221196";
                String hashCode = "NzcwNzM0NDkyNDA4NTQwMjkwNzE4MDM0NDA0MTE0MTM2OTA3OTk5";
                String merchantSecret = PayHere.generateMD5(hashCode);

                // set payhere object
                JsonObject payhereData = new JsonObject();
                payhereData.addProperty("merchant_id", merchantId);
                payhereData.addProperty("sandbox", true);
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
                payhereData.addProperty("order_id", orderIDString);
                payhereData.addProperty("items", product.getTitle());
                payhereData.addProperty("currency", currency);
                payhereData.addProperty("amount", formatedAmount);

                String md5Hash = PayHere.generateMD5(merchantId + orderIDString + formatedAmount + currency + merchantSecret);
                payhereData.addProperty("hash", md5Hash);

                responseObject.add("payhere_data", payhereData);
                session.beginTransaction().commit();
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
