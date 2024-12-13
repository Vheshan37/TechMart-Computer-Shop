package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
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

@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Gson gson = new Gson();

        String action = req.getParameter("action");
        JsonObject responseObj = new JsonObject();
        responseObj.add("cityList", gson.toJsonTree(getCities(session)));
        responseObj.add("districtList", gson.toJsonTree(getDistricts(session)));
        if (action.equals("get")) {

            if (req.getSession().getAttribute("tm_user") != null) {

                User_DTO user = (User_DTO) req.getSession().getAttribute("tm_user");
                int userId = user.getId();

                User db_user = (User) session.get(User.class, userId);
                db_user.setPassword(null);
                db_user.setVerification(null);
                responseObj.add("user", gson.toJsonTree(db_user));

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
