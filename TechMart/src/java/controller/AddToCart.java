package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
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
import model.entity.Product;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        String productID = req.getParameter("id");
        User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");

        if (req.getSession().getAttribute("tm_user") != null) {
            Criteria cartTable = session.createCriteria(Cart.class);
            cartTable.add(Restrictions.and(
                    Restrictions.eq("product", (Product) session.get(Product.class, Integer.valueOf(productID))),
                    Restrictions.eq("user", (User) session.get(User.class, user.getId()))
            ));

            if (cartTable.list().isEmpty()) {
                Cart cart = new Cart();
                cart.setProduct((Product) session.get(Product.class, Integer.valueOf(productID)));
                cart.setUser((User) session.get(User.class, user.getId()));
                cart.setQuantity(1);

                session.save(cart);
                session.beginTransaction().commit();

                responseObject.setSuccess(true);
                responseObject.setContent("The item has been successfully added to your cart.");
            } else {
                responseObject.setContent("This item is already in your cart.");
            }
        } else {
            Product product = (Product) session.get(Product.class, Integer.valueOf(productID));

            if (req.getSession().getAttribute("tm_cart") != null) {
                // Found Session Cart
                List<Cart_DTO> sessionCart = (List<Cart_DTO>) req.getSession().getAttribute("tm_cart");

                boolean isCartProduct = false;
                Cart_DTO sessionCart_DTO = null;

                for (Cart_DTO sessionItem : sessionCart) {
                    if (product.getId() == sessionItem.getProduct().getId()) {
                        isCartProduct = true;
                        sessionCart_DTO = sessionItem;
                        break;
                    }
                }

                if (isCartProduct) { // Product in the cart
                    int currentQuantity = sessionCart_DTO.getQuantity();
                    sessionCart_DTO.setQuantity(currentQuantity + 1);

                    responseObject.setSuccess(true);
                    responseObject.setContent("Increase the quantity.");
                } else { // Product not in the cart
                    Cart_DTO cart_DTO = new Cart_DTO();
                    cart_DTO.setProduct(product);
                    cart_DTO.setQuantity(1);
                    sessionCart.add(cart_DTO);

                    responseObject.setSuccess(true);
                    responseObject.setContent("The item has been successfully added to your cart.");
                }

                req.getSession().setAttribute("tm_cart", sessionCart);

            } else {
                // Empty Session Cart
                List<Cart_DTO> sessionCart = new ArrayList<>();
                Cart_DTO cart_DTO = new Cart_DTO();
                cart_DTO.setProduct(product);
                cart_DTO.setQuantity(1);
                sessionCart.add(cart_DTO);
                req.getSession().setAttribute("tm_cart", sessionCart);

                responseObject.setSuccess(true);
                responseObject.setContent("The item has been successfully added to your cart.");
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
