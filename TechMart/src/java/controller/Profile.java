package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.User_DTO;
import model.entity.Address;
import model.entity.City;
import model.entity.District;
import model.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Gson gson = new Gson();

        JsonObject responseObj = new JsonObject();
        responseObj.add("cityList", gson.toJsonTree(getCities(session)));
        responseObj.add("districtList", gson.toJsonTree(getDistricts(session)));

        if (req.getSession().getAttribute("tm_user") != null) {

            User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
            int userId = user.getId();

            User db_user = (User) session.get(User.class, userId);
            db_user.setPassword(null);
            db_user.setVerification(null);
            responseObj.add("user", gson.toJsonTree(db_user));

            Criteria userAddressTable = session.createCriteria(Address.class);
            userAddressTable.add(Restrictions.eq("user", db_user));

            if (!userAddressTable.list().isEmpty()) {
                Address address = (Address) userAddressTable.uniqueResult();
                responseObj.add("address", gson.toJsonTree(address));
            }
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObj));

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Gson gson = new Gson();

        JsonObject responseObj = new JsonObject();

        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String mobile = req.getParameter("mobile");
        String email = req.getParameter("email");
        String line1 = req.getParameter("line1");
        String line2 = req.getParameter("line2");
        String cityId = req.getParameter("city");
        String postalCode = req.getParameter("postal_code");

        if (firstName == null || firstName.isEmpty()
                || lastName == null || lastName.isEmpty()
                || mobile == null || mobile.isEmpty()
                || email == null || email.isEmpty()
                || line1 == null || line1.isEmpty()
                || cityId == null || cityId.isEmpty()
                || postalCode == null || postalCode.isEmpty()) {

            responseObj.addProperty("status", "error");
            responseObj.addProperty("message", "All fields are required.");
        } else {
            try {
                session.beginTransaction();

                // Retrieve the logged-in user
                User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
                if (user == null) {
                    responseObj.addProperty("status", "error");
                    responseObj.addProperty("message", "User not logged in.");
                    session.getTransaction().rollback();
                    return;
                }

                int userId = user.getId();
                User db_user = (User) session.get(User.class, userId);
                if (db_user == null) {
                    responseObj.addProperty("status", "error");
                    responseObj.addProperty("message", "User not found.");
                    session.getTransaction().rollback();
                    return;
                }

                // Update User entity
                db_user.setFirst_name(firstName);
                db_user.setLast_name(lastName);
                db_user.setMobile(mobile);
                db_user.setEmail(email);
                session.update(db_user);

                // Update or create Address entity
                Criteria addressCriteria = session.createCriteria(Address.class);
                addressCriteria.add(Restrictions.eq("user", db_user));
                Address address;

                if (!addressCriteria.list().isEmpty()) {
                    address = (Address) addressCriteria.uniqueResult();
                } else {
                    address = new Address();
                    address.setUser(db_user);
                }

                address.setLine1(line1);
                address.setLine2(line2);
                address.setPostalCode(postalCode);

                City city = (City) session.get(City.class, Integer.parseInt(cityId));
                if (city == null) {
                    responseObj.addProperty("status", "error");
                    responseObj.addProperty("message", "Invalid city selected.");
                    session.getTransaction().rollback();
                    return;
                }
                address.setCity(city);

                session.saveOrUpdate(address);

                // Commit transaction
                session.getTransaction().commit();

                // Prepare response
                responseObj.addProperty("status", "success");
                responseObj.addProperty("message", "Profile updated successfully.");
            } catch (Exception e) {
                e.printStackTrace();
                session.getTransaction().rollback();
                responseObj.addProperty("status", "error");
                responseObj.addProperty("message", "An error occurred while updating the profile.");
            }
        }

        session.close();

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObj));
    }

    private List<City> getCities(Session session) {
        Criteria cityTable = session.createCriteria(City.class);
        List<City> cityList = cityTable.list();
        return cityList;
    }

    private List<District> getDistricts(Session session) {
        Criteria districtTable = session.createCriteria(District.class);
        List<District> districtList = districtTable.list();
        return districtList;
    }
}
