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
import model.Mail;
import model.PasswordValidator;
import model.dto.Response_DTO;
import model.dto.User_DTO;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Response_DTO responseObject = new Response_DTO();

        User_DTO requestUser = gson.fromJson(req.getReader(), User_DTO.class);

        if (requestUser.getEmail().isEmpty()) {
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
            criteria.add(Restrictions.and(
                    Restrictions.eq("email", requestUser.getEmail()),
                    Restrictions.eq("password", requestUser.getPassword())
            ));

            if (criteria.list().isEmpty()) {
                responseObject.setContent("Invalid email or password");
            } else {
                User user = (User) criteria.list().get(0);
                if (user.getVerification().equals("verified") || user.getVerification() == "verified") {
                    requestUser.setPassword(null);
                    requestUser.setId(user.getId());
                    requestUser.setFirst_name(user.getFirst_name());
                    requestUser.setLast_name(user.getLast_name());

                    req.getSession().setAttribute("tm_user", requestUser);
                } else {
                    req.getSession().setAttribute("tm_email", user.getEmail());

                    int code = (int) (Math.random() * 900000) + 100000;
                    String content = "<div>Use this verification code to verify your account</div>"
                            + "<span>" + code + "</span>";
                    Mail.sendMail(req.getSession().getAttribute("tm_email").toString(), "Tech Mart Account Verification", content);

                    user.setVerification(String.valueOf(code));

                    session.update(user);
                    session.beginTransaction().commit();

                    responseObject.setContent("verification_method");
                }
                responseObject.setSuccess(true);
            }
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
