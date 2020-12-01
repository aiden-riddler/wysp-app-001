package com.aidenriddler.wysp;

import java.util.Date;
import java.util.Map;

public class Shop {

    private String shopID;
    private String shopName;
    private String email;
    private String country;
    private String city;
    private Map<String,String> shopTypes;
    private Date timestamp;
    private String currency;

    public Shop() {
    }

    public Shop(String shopID, String shopName, String email, String country, String city, Map<String, String> shopTypes, Date timestamp, String currency) {
        this.shopID = shopID;
        this.shopName = shopName;
        this.email = email;
        this.country = country;
        this.city = city;
        this.shopTypes = shopTypes;
        this.timestamp = timestamp;
        this.currency = currency;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, String> getShopTypes() {
        return shopTypes;
    }

    public void setShopTypes(Map<String, String> shopTypes) {
        this.shopTypes = shopTypes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
