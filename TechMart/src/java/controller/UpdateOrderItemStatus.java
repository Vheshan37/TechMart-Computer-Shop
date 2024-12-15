package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.entity.OrderItem;
import model.entity.OrderItemStatus;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "UpdateOrderItemStatus", urlPatterns = {"/UpdateOrderItemStatus"})
@MultipartConfig
public class UpdateOrderItemStatus extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // initializing stage
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        // processing stage
        responseObject.addProperty("reload", false);
        Transaction transaction = session.beginTransaction();

        int orderItemID = Integer.parseInt(req.getParameter("orderItemID"));
        String action = req.getParameter("action");

        Criteria orderItemTable = session.createCriteria(OrderItem.class);
        orderItemTable.add(Restrictions.eq("id", orderItemID));
        OrderItem orderItem = (OrderItem) orderItemTable.uniqueResult();

        OrderItemStatus orderItemStatus = orderItem.getStatus();
        if (action.equals("up")) {
            if (orderItemStatus.getId() < 5) {
                Criteria orderItemStatusTable = session.createCriteria(OrderItemStatus.class);
                orderItemStatusTable.add(Restrictions.eq("id", orderItem.getStatus().getId() + 1));
                OrderItemStatus nextStatus = (OrderItemStatus) orderItemStatusTable.uniqueResult();
                orderItem.setStatus(nextStatus);
                session.update(orderItem);
                responseObject.addProperty("reload", true);
            }
        } else if (action.equals("down")) {
            if (orderItemStatus.getId() > 1) {
                Criteria orderItemStatusTable = session.createCriteria(OrderItemStatus.class);
                orderItemStatusTable.add(Restrictions.eq("id", orderItem.getStatus().getId() - 1));
                OrderItemStatus previousStatus = (OrderItemStatus) orderItemStatusTable.uniqueResult();
                orderItem.setStatus(previousStatus);
                session.update(orderItem);
                responseObject.addProperty("reload", true);
            }
        }

        resp.sendRedirect("Orders");

        // finalizing stage
        transaction.commit();
        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
