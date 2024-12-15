package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.Cart_DTO;
import model.dto.Response_DTO;
import model.dto.User_DTO;
import model.entity.Cart;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "CartLoad", urlPatterns = {"/CartLoad"})
public class CartLoad extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        if (req.getSession().getAttribute("tm_user") == null) {
            if (req.getSession().getAttribute("tm_cart") != null) {
                List<Cart_DTO> sessionCart = (List<Cart_DTO>) req.getSession().getAttribute("tm_cart");
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("cartList", gson.toJsonTree(sessionCart));

                responseObject.setSuccess(true);
                responseObject.setContent(jsonObject);
            }
            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(responseObject));
        } else {
            User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");

            Criteria cartTable = session.createCriteria(Cart.class);
            cartTable.add(Restrictions.eq("user", session.get(User.class, user.getId())));
            List<Cart> cartList = cartTable.list();

            if (!cartList.isEmpty()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("cartList", gson.toJsonTree(cartList));

                responseObject.setSuccess(true);
                responseObject.setContent(jsonObject);
            }

            resp.setContentType("application/json");
            resp.getWriter().write(gson.toJson(responseObject));
        }
    }
}
