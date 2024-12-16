package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.User_DTO;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "loadPurchasingHistory", urlPatterns = {"/loadPurchasingHistory"})
public class loadPurchasingHistory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        if (req.getSession().getAttribute("tm_user") != null) {
            User user = getUser(session, req, responseObject, gson); //get user   
            List<Order> ordersList = getOrderList(session, user);
            responseObject.add("orderList", gson.toJsonTree(ordersList));
            List<OrderItem> orderItemList = getOrderItemsFromOrders(session, ordersList);
            responseObject.add("orderItemList", gson.toJsonTree(orderItemList));
        } else {
            responseObject.addProperty("login_status", "Invalid user");
        }

        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

    public User getUser(Session session, HttpServletRequest req, JsonObject responseObject, Gson gson) {
        User_DTO userDTO = (User_DTO) req.getSession().getAttribute("tm_user");
        User user = (User) session.get(User.class, userDTO.getId());
        responseObject.add("user", gson.toJsonTree(user));

        return user;
    }

    public List<Order> getOrderList(Session session, User user) {
        Criteria orderTable = session.createCriteria(Order.class);
        orderTable.add(Restrictions.eq("user", session.get(User.class, user.getId())));
        List<Order> orderList = orderTable.list();
        return orderList;
    }

    public List<OrderItem> getOrderItemsFromOrders(Session session, List<Order> orderList) {
        List<OrderItem> allOrderItems = new ArrayList<>();

        for (Order order : orderList) {
            order.getUser().setPassword(null);
            order.getUser().setVerification(null);
            Criteria orderItemCriteria = session.createCriteria(OrderItem.class);
            orderItemCriteria.add(Restrictions.eq("order", order)); // Match Order in OrderItem
            List<OrderItem> orderItems = orderItemCriteria.list();  // Fetch OrderItems for this Order
            for (OrderItem orderItem : orderItems) {
                orderItem.getOrder().getUser().setPassword(null);
                orderItem.getOrder().getUser().setVerification(null);
            }
            allOrderItems.addAll(orderItems); // Add them to the combined list
        }

        return allOrderItems;
    }

}
