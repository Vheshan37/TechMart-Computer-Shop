package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.EmailValidator;
import model.HibernateUtil;
import model.PasswordValidator;
import model.dto.Response_DTO;
import model.dto.User_DTO;
import model.entity.User;
import model.validators.MobileValidator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        User_DTO requestUser = gson.fromJson(req.getReader(), User_DTO.class);

        if (requestUser.getFirst_name().isEmpty()) {
            responseObject.setContent("Please enter your First name");
        } else if (requestUser.getLast_name().isEmpty()) {
            responseObject.setContent("Please enter your Last name");
        } else if (requestUser.getMobile().isEmpty()) {
            responseObject.setContent("Please fill your Mobile number");
        } else if (!MobileValidator.validate(requestUser.getMobile())) {
            responseObject.setContent("Invalid Mobile number");
        } else if (requestUser.getEmail().isEmpty()) {
            responseObject.setContent("Please fill your Email");
        } else if (!EmailValidator.validate(requestUser.getEmail())) {
            responseObject.setContent("Invalid Email");
        } else if (requestUser.getPassword().isEmpty()) {
            responseObject.setContent("Please fill your Password");
        } else if (!PasswordValidator.validatePassword(requestUser.getPassword(), 8, true, true)) {
            responseObject.setContent("Password must be at least 8 characters long and "
                    + "include at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        } else {
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", requestUser.getEmail()));

            if (!criteria.list().isEmpty()) {
                responseObject.setContent("User with this email already exists");
            } else {
                User user = new User();
                user.setFirst_name(requestUser.getFirst_name());
                user.setLast_name(requestUser.getLast_name());
                user.setMobile(requestUser.getMobile());
                user.setEmail(requestUser.getEmail());
                user.setPassword(requestUser.getPassword());
                user.setVerification("false");

                session.save(user);
                session.beginTransaction().commit();

                responseObject.setSuccess(true);
            }
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }
}
