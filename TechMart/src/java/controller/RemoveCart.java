package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.Cart_DTO;
import model.dto.Response_DTO;
import model.entity.Cart;
import model.entity.Product;
import org.hibernate.Session;

@WebServlet(name = "RemoveCart", urlPatterns = {"/RemoveCart"})
public class RemoveCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        if (req.getSession().getAttribute("tm_user") != null) {
            Cart cart = (Cart) session.get(Cart.class, Integer.valueOf(req.getParameter("id")));
            if (cart != null) {
                session.delete(cart);
                responseObject.setSuccess(true);
                responseObject.setContent("Item removed.");

                session.beginTransaction().commit();
            }
        } else if (req.getSession().getAttribute("tm_cart") != null) {
            String productID = req.getParameter("id");
            Product product = (Product) session.get(Product.class, Integer.valueOf(productID));
            List<Cart_DTO> sessionCart = (List<Cart_DTO>) req.getSession().getAttribute("tm_cart");
            for (Cart_DTO sessionCartItem : sessionCart) {
                if (sessionCartItem.getProduct().getId() == product.getId()) {
                    responseObject.setSuccess(true);
                    responseObject.setContent("Item removed. from your session");
                    break;
                }
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
