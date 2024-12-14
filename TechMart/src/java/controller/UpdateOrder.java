package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.entity.Order;
import model.entity.OrderStatus;
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
        Order order = (Order) session.get(Order.class, orderId);
        order.setStatus(orderStatus);

        session.update(order);
        session.beginTransaction().commit();

        responseObject.addProperty("success", true);

        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
