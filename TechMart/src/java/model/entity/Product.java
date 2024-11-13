package model.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "qty", nullable = false)
    private int quantity;

    @Column(name = "delivery_in", nullable = false)
    private double deliveryIn;

    @Column(name = "delivery_out", nullable = false)
    private double deliveryOut;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "category_has_brand_id")
    private CategoryHasBrand categoryHasBrand;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date_time", nullable = false)
    private Date dateTime;

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getDeliveryIn() {
        return deliveryIn;
    }

    public void setDeliveryIn(double deliveryIn) {
        this.deliveryIn = deliveryIn;
    }

    public double getDeliveryOut() {
        return deliveryOut;
    }

    public void setDeliveryOut(double deliveryOut) {
        this.deliveryOut = deliveryOut;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public CategoryHasBrand getCategoryHasBrand() {
        return categoryHasBrand;
    }

    public void setCategoryHasBrand(CategoryHasBrand categoryHasBrand) {
        this.categoryHasBrand = categoryHasBrand;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

}
