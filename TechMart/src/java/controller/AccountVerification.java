package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.Response_DTO;
import model.dto.User_DTO;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AccountVerification", urlPatterns = {"/AccountVerification"})
public class AccountVerification extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        String email = String.valueOf(req.getSession().getAttribute("tm_email"));
        String verification = req.getParameter("code");

        System.out.println(email);
        System.out.println(verification);

        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.and(
                Restrictions.eq("email", email),
                Restrictions.eq("verification", verification)
        ));

        if (criteria.list().isEmpty()) {
            responseObject.setContent("Verification Failed. Please try again later.");
        } else {

            User user = (User) criteria.list().get(0);
            user.setVerification("verified");
            session.update(user);
            session.beginTransaction().commit();

            User_DTO requestUser = new User_DTO();

            requestUser.setPassword(null);
            requestUser.setId(user.getId());
            requestUser.setEmail(user.getEmail());
            requestUser.setFirst_name(user.getFirst_name());
            requestUser.setLast_name(user.getLast_name());

            req.getSession().setAttribute("tm_user", requestUser);

            responseObject.setSuccess(true);
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
