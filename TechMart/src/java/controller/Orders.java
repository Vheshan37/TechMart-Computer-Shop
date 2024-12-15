package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Orders", urlPatterns = {"/Orders"})
public class Orders extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // initial stage
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        // processing stage
        Transaction transaction = session.beginTransaction();

        // code here...
        User seller = getSeller(session, req, responseObject, gson);
        List<Order> orderList = getOrderList(session, seller);
        responseObject.add("orderList", gson.toJsonTree(orderList));
        List<OrderItem> allOrderItems = getOrderItemsFromOrders(session, orderList);
        responseObject.add("orderItemList", gson.toJsonTree(allOrderItems));

        // finalizing stage
        transaction.commit();
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

    public User getSeller(Session session, HttpServletRequest req, JsonObject responseObject, Gson gson) {
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
            Criteria orderItemCriteria = session.createCriteria(OrderItem.class);
            orderItemCriteria.add(Restrictions.eq("order", order)); // Match Order in OrderItem
            List<OrderItem> orderItems = orderItemCriteria.list();  // Fetch OrderItems for this Order
            allOrderItems.addAll(orderItems); // Add them to the combined list
        }

        return allOrderItems;
    }

}
