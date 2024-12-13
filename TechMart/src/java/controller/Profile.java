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
import model.dto.User_DTO;
import model.entity.Address;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Gson gson = new Gson();

        String action = req.getParameter("action");
        JsonObject responseObj = new JsonObject();
        if (action.equals("get")) {

            if (req.getSession().getAttribute("tm_user") != null) {

                User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
                responseObj.add("user", gson.toJsonTree(user));

                Criteria userAddressTable = session.createCriteria(Address.class);
                userAddressTable.add(Restrictions.eq("user", user));

                if (!userAddressTable.list().isEmpty()) {
                    Address address = (Address) userAddressTable.uniqueResult();
                    responseObj.add("address", gson.toJsonTree(address));
                }
            }
        } else {
            System.out.println(action);
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObj));

    }
}
