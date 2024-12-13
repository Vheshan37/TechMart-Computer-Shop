package controller;

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

        String action = req.getParameter("action");
        if (action.equals("get")) {

            if (req.getSession().getAttribute("tm_user") != null) {
                User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");

                Criteria userAddressTable = session.createCriteria(Address.class);
                userAddressTable.add(Restrictions.eq("user", user));

                if (!userAddressTable.list().isEmpty()) {
                    
                }
            }
        } else {
            System.out.println(action);
        }

    }
}
