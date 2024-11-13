package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.dto.Response_DTO;
import model.entity.Brand;
import model.entity.Category;
import model.entity.CategoryHasBrand;
import model.entity.Color;
import model.entity.District;
import model.entity.Feature;
import model.entity.FeatureList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

@WebServlet(name = "LoadRegistrationFeature", urlPatterns = {"/LoadRegistrationFeature"})
public class LoadRegistrationFeature extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers().create();
        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria category = session.createCriteria(Category.class);
        category.addOrder(Order.asc("category"));
        List<Category> categoryList = category.list();

        Criteria feature = session.createCriteria(Feature.class);
        feature.addOrder(Order.asc("feature"));
        List<Feature> featureList = feature.list();

        Criteria categoryFeature = session.createCriteria(FeatureList.class);
        List<FeatureList> categoryFeatureList = categoryFeature.list();

        Criteria brand = session.createCriteria(Brand.class);
        brand.addOrder(Order.asc("brand"));
        List<Brand> brandList = brand.list();

        Criteria color = session.createCriteria(Color.class);
        color.addOrder(Order.asc("color"));
        List<Color> colorList = color.list();

        Criteria categoryBrand = session.createCriteria(CategoryHasBrand.class);
        List<CategoryHasBrand> categoryBrandList = categoryBrand.list();

        Criteria district = session.createCriteria(District.class);
        district.addOrder(Order.asc("district"));
        List<District> districtList = district.list();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("featureList", gson.toJsonTree(featureList));
        jsonObject.add("categoryFeatureList", gson.toJsonTree(categoryFeatureList));
        jsonObject.add("brandList", gson.toJsonTree(brandList));
        jsonObject.add("colorList", gson.toJsonTree(colorList));
        jsonObject.add("categoryBrandList", gson.toJsonTree(categoryBrandList));
        jsonObject.add("districtList", gson.toJsonTree(districtList));

        session.close();

        resp.setContentType("application.json");
        resp.getWriter().write(gson.toJson(jsonObject));

    }

}
