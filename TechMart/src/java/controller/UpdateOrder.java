package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.User_DTO;
import model.entity.Cart;
import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateOrder", urlPatterns = {"/UpdateOrder"})
public class UpdateOrder extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        responseObject.addProperty("success", false);

        Criteria orderStatusTable = session.createCriteria(OrderStatus.class);
        orderStatusTable.add(Restrictions.eq("status", "paid"));
        OrderStatus orderStatus = (OrderStatus) orderStatusTable.uniqueResult();

        String orderId = req.getParameter("id");
        Order order = (Order) session.get(Order.class, Integer.parseInt(orderId));
        order.setStatus(orderStatus);

        User user = getUser(session, req, responseObject, gson);
        List<Cart> cartList = getCartList(session, user);
        removeCartItem(cartList, session);

        session.update(order);
        session.beginTransaction().commit();

        responseObject.addProperty("success", true);

        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

    public void removeCartItem(List<Cart> cartList, Session session) {
        for (Cart cart : cartList) {
            session.delete(cart);
        }
    }

    public List<Cart> getCartList(Session session, User user) {
        Criteria cartTable = session.createCriteria(Cart.class);
        cartTable.add(Restrictions.eq("user", session.get(User.class, user.getId())));
        List<Cart> cartList = cartTable.list();
        return cartList;
    }

    public User getUser(Session session, HttpServletRequest req, JsonObject responseObject, Gson gson) {
        User_DTO userDTO = (User_DTO) req.getSession().getAttribute("tm_user");
        User user = (User) session.get(User.class, userDTO.getId());
        responseObject.add("user", gson.toJsonTree(user));

        return user;
    }
}
