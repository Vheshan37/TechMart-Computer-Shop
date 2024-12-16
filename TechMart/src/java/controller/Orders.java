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
        User seller = getSeller(session, req, responseObject, gson);

        List<OrderItem> orderItems = getSellerOrders(session, req, responseObject, gson);
        responseObject.add("orderItemList", gson.toJsonTree(orderItems));

        // finalizing stage
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

    public User getSeller(Session session, HttpServletRequest req, JsonObject responseObject, Gson gson) {
        User_DTO userDTO = (User_DTO) req.getSession().getAttribute("tm_user");
        User user = (User) session.get(User.class, userDTO.getId());
        user.setPassword(null);
        user.setVerification(null);
        responseObject.add("user", gson.toJsonTree(user));

        return user;
    }
    
    public List<OrderItem> getSellerOrders(Session session, HttpServletRequest req, JsonObject responseObject, Gson gson) {
        Criteria orderItemCriteria = session.createCriteria(OrderItem.class);
        List<OrderItem> orderItems = orderItemCriteria.list();  // Fetch OrderItems for this Order

        List<OrderItem> allOrderItems = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProduct().getUser() == getSeller(session, req, responseObject, gson)) {
                allOrderItems.add(orderItem);
            }
        }

        return allOrderItems;
    }

}
