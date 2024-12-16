package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.User_DTO;
import model.entity.Address;
import model.entity.Order;
import model.entity.OrderItem;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadInvoice", urlPatterns = {"/LoadInvoice"})
public class LoadInvoice extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        Session session = HibernateUtil.getSessionFactory().openSession();

        int orderID = Integer.parseInt(req.getParameter("id"));
        Order order = getOrder(session, orderID);
        responseObject.add("order", gson.toJsonTree(order));
        responseObject.add("orderItems", gson.toJsonTree(getOrderItems(session, order)));
        responseObject.add("address", gson.toJsonTree(getUserAddress(session, order)));

        session.close();
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

    public Order getOrder(Session session, int orderID) {
        Order order = (Order) session.get(Order.class, orderID);
        order.getUser().setPassword(null);
        order.getUser().setVerification(null);
        return order;
    }

    public List<OrderItem> getOrderItems(Session session, Order order) {
        Criteria orderItemTable = session.createCriteria(OrderItem.class);
        orderItemTable.add(Restrictions.eq("order", order));
        List<OrderItem> orderItemList = orderItemTable.list();
        return orderItemList;
    }

    public Address getUserAddress(Session session, Order order) {
        Criteria addressTable = session.createCriteria(Address.class);
        addressTable.add(Restrictions.eq("user", order.getUser()));
        Address address = (Address) addressTable.uniqueResult();
        return address;
    }
}
