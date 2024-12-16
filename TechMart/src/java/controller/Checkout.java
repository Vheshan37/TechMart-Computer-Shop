package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.PayHere;
import model.dto.User_DTO;
import model.entity.Address;
import model.entity.Cart;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.OrderItemStatus;
import model.entity.OrderStatus;
import model.entity.Product;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        Transaction transaction = session.beginTransaction();

        // user login validation
        if (req.getSession().getAttribute("tm_user") != null) {

            User user = getUser(session, req, responseObject, gson); //get user
            Address userAddress = getUserAddress(session, user); // get user address

            if (userAddress != null) {
                responseObject.addProperty("payment_status", true);

                List<Cart> cartList = getCartList(session, user); // get cart list
                if (!cartList.isEmpty()) {
                    OrderStatus orderStatus = getOrderStatus(session); // get order status
                    Order order = createNewOrder(user, orderStatus, session); // create new order

                    double cartAmount = 0;
                    double deliveryFee = 0;
                    for (Cart cart : cartList) {
                        cartAmount += cart.getProduct().getPrice() * cart.getQuantity();
                        handleOrderItem(cart, order, session);
                        deliveryFee += getDeliveryCost(userAddress, cart.getProduct(), responseObject);
                    }
                    double totalAmount = cartAmount + deliveryFee;
                    updatePaidAmount(totalAmount, order, session);

                    handlePaymentProcess(totalAmount, order, user, userAddress, responseObject); // payhere process
                }
            } else {
                responseObject.addProperty("login_status", "Incomplete profile details");
            }
        } else {
            responseObject.addProperty("login_status", "Invalid user");
        }

        transaction.commit();
        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

    private void updatePaidAmount(double totalAmount, Order order, Session session) {
        order.setPaidAmount(totalAmount);
        session.update(order);
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

    public User getUser(Session session, HttpServletRequest req, JsonObject responseObject, Gson gson) {
        User_DTO userDTO = (User_DTO) req.getSession().getAttribute("tm_user");
        User user = (User) session.get(User.class, userDTO.getId());
        responseObject.add("user", gson.toJsonTree(user));

        return user;
    }

    public Address getUserAddress(Session session, User user) {
        Criteria userAddressCriteria = session.createCriteria(Address.class);
        userAddressCriteria.add(Restrictions.eq("user", user));
        Address userAddress = (Address) userAddressCriteria.uniqueResult();
        return userAddress;
    }

    public List<Cart> getCartList(Session session, User user) {
        Criteria cartTable = session.createCriteria(Cart.class);
        cartTable.add(Restrictions.eq("user", session.get(User.class, user.getId())));
        List<Cart> cartList = cartTable.list();
        return cartList;
    }

    public OrderStatus getOrderStatus(Session session) {
        Criteria orderStatusCriteria = session.createCriteria(OrderStatus.class);
        orderStatusCriteria.add(Restrictions.eq("status", "pending"));
        OrderStatus orderStatus = (OrderStatus) orderStatusCriteria.uniqueResult();
        return orderStatus;
    }

    public OrderItemStatus getOrderItemStatus(Session session) {
        Criteria orderItemStatusCriteria = session.createCriteria(OrderItemStatus.class);
        orderItemStatusCriteria.add(Restrictions.eq("status", "Pending"));
        OrderItemStatus orderItemStatus = (OrderItemStatus) orderItemStatusCriteria.uniqueResult();
        return orderItemStatus;
    }

    public Order createNewOrder(User user, OrderStatus orderStatus, Session session) {
        Order order = new Order();
        order.setDateTime(new Date());
        order.setUser(user);
        order.setStatus(orderStatus);
        session.save(order);
        return order;
    }

    public void handleOrderItem(Cart cart, Order order, Session session) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(cart.getProduct());
        orderItem.setQty(cart.getQuantity());
        orderItem.setOrder(order);
        orderItem.setStatus(getOrderItemStatus(session));
        session.save(orderItem);
        session.delete(cart);
    }

    public void handlePaymentProcess(double cartAmount, Order order, User user, Address userAddress, JsonObject responseObject) {
        String formattedAmount = new DecimalFormat("0.00").format(cartAmount);
        String orderIDString = String.valueOf(order.getId());
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
        payhereData.addProperty("first_name", user.getFirst_name());
        payhereData.addProperty("last_name", user.getLast_name());
        payhereData.addProperty("email", user.getEmail());
        payhereData.addProperty("phone", user.getMobile());
        payhereData.addProperty("address", userAddress.getLine1() + ", " + userAddress.getLine2() + "." + userAddress.getCity().getCity());
        payhereData.addProperty("city", userAddress.getCity().getCity());
        payhereData.addProperty("country", "Sri Lanka");
        payhereData.addProperty("order_id", orderIDString);
        payhereData.addProperty("items", "Cart Items. Order: " + order.getId());
        payhereData.addProperty("currency", currency);
        payhereData.addProperty("amount", formattedAmount);
        payhereData.addProperty("hash", md5Hash);

        responseObject.add("payhere_data", payhereData);
    }

}
