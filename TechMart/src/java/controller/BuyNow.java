package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.HibernateUtil;
import model.PayHere;
import model.dto.User_DTO;
import model.entity.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

@WebServlet(name = "BuyNow", urlPatterns = {"/BuyNow"})
public class BuyNow extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        String productId = req.getParameter("id");
        Product product = getProductById(session, productId);

        if (product == null) {
            responseObject.addProperty("error", "Product not found");
            returnResponse(resp, gson, responseObject);
            return;
        }

        handleUserLogin(req, resp, gson, responseObject, session, product);
    }

    private Product getProductById(Session session, String productId) {
        return (Product) session.get(Product.class, Integer.valueOf(productId));
    }

    private void handleUserLogin(HttpServletRequest req, HttpServletResponse resp, Gson gson, JsonObject responseObject, Session session, Product product) throws IOException {
        responseObject.addProperty("payment_status", false);
        if (req.getSession().getAttribute("tm_user") != null) {
            User_DTO userDTO = (User_DTO) req.getSession().getAttribute("tm_user");
            User user = getUserById(session, userDTO.getId());
            responseObject.add("user", gson.toJsonTree(user));

            Address userAddress = getUserAddress(session, user);
            if (userAddress != null) {
                responseObject.addProperty("payment_status", true);
                handleOrderAndPayment(session, product, userDTO, user, userAddress, responseObject);
            } else {
                responseObject.addProperty("login_status", "Incomplete profile details");
            }
        } else {
            responseObject.addProperty("login_status", "Invalid user");
        }

        returnResponse(resp, gson, responseObject);
    }

    private User getUserById(Session session, int userId) {
        return (User) session.get(User.class, userId);
    }

    private Address getUserAddress(Session session, User user) {
        Criteria userAddressCriteria = session.createCriteria(Address.class);
        userAddressCriteria.add(Restrictions.eq("user", user));
        return (Address) userAddressCriteria.uniqueResult();
    }

    private void handleOrderAndPayment(Session session, Product product, User_DTO userDTO, User user, Address userAddress, JsonObject responseObject) {
        double productCost = product.getPrice();
        double deliveryCost = getDeliveryCost(userAddress, product, responseObject);

        productCost += deliveryCost;

        OrderStatus orderStatus = getOrderStatus(session);

        Order order = createOrder(session, user, orderStatus, product, productCost);

        responseObject.addProperty("orderID", order.getId());

        JsonObject payhereData = createPayhereData(userDTO, userAddress, product, productCost, order.getId());

        responseObject.add("payhere_data", payhereData);

        session.beginTransaction().commit();
    }

    private double getDeliveryCost(Address userAddress, Product product, JsonObject responseObject) {
        double deliveryCost;
        if (product.getDistrict().getId() == userAddress.getCity().getDistrict().getId()) {
            deliveryCost = product.getDeliveryIn();
            responseObject.addProperty("delivery_cost", deliveryCost);
        } else {
            deliveryCost = product.getDeliveryOut();
            responseObject.addProperty("delivery_cost", deliveryCost);
        }
        return deliveryCost;
    }

    private OrderStatus getOrderStatus(Session session) {
        Criteria orderStatusCriteria = session.createCriteria(OrderStatus.class);
        orderStatusCriteria.add(Restrictions.eq("status", "pending"));
        return (OrderStatus) orderStatusCriteria.uniqueResult();
    }

    private OrderItemStatus getOrderItemStatus(Session session) {
        Criteria orderItemStatusCriteria = session.createCriteria(OrderItemStatus.class);
        orderItemStatusCriteria.add(Restrictions.eq("status", "pending"));
        return (OrderItemStatus) orderItemStatusCriteria.uniqueResult();
    }

    private Order createOrder(Session session, User user, OrderStatus orderStatus, Product product, double productCost) {
        Order order = new Order();
        order.setDateTime(new Date());
        order.setUser(user);
        order.setStatus(orderStatus);
        order.setPaidAmount(productCost);
        session.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQty(1);
        orderItem.setOrder(order);
        orderItem.setStatus(getOrderItemStatus(session));
        session.save(orderItem);

        return order;
    }

    private JsonObject createPayhereData(User_DTO userDTO, Address userAddress, Product product, double productCost, int orderId) {
        String formattedAmount = new DecimalFormat("0.00").format(productCost);
        String orderIDString = String.valueOf(orderId);
        String currency = "LKR";
        String merchantId = "1221196";
        String hashCode = "NzcwNzM0NDkyNDA4NTQwMjkwNzE4MDM0NDA0MTE0MTM2OTA3OTk5";
        String merchantSecret = PayHere.generateMD5(hashCode);

        String md5Hash = PayHere.generateMD5(merchantId + orderIDString + formattedAmount + currency + merchantSecret);

        JsonObject payhereData = new JsonObject();
        payhereData.addProperty("merchant_id", merchantId);
        payhereData.addProperty("sandbox", true);
        payhereData.addProperty("return_url", "index.html");
        payhereData.addProperty("cancel_url", "index.html");
        payhereData.addProperty("notify_url", "index.html");
        payhereData.addProperty("first_name", userDTO.getFirst_name());
        payhereData.addProperty("last_name", userDTO.getLast_name());
        payhereData.addProperty("email", userDTO.getEmail());
        payhereData.addProperty("phone", userDTO.getMobile());
        payhereData.addProperty("address", getFullAddress(userAddress));
        payhereData.addProperty("city", userAddress.getCity().getCity());
        payhereData.addProperty("country", "Sri Lanka");
        payhereData.addProperty("order_id", orderIDString);
        payhereData.addProperty("items", product.getTitle());
        payhereData.addProperty("currency", currency);
        payhereData.addProperty("amount", formattedAmount);
        payhereData.addProperty("hash", md5Hash);

        return payhereData;
    }

    private String getFullAddress(Address userAddress) {
        return userAddress.getLine1() + ", " + userAddress.getLine2() + ", " + userAddress.getCity().getCity()
                + ". [" + userAddress.getCity().getDistrict().getDistrict() + "]";
    }

    private void returnResponse(HttpServletResponse resp, Gson gson, JsonObject responseObject) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
