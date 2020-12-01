package com.aidenriddler.wysp;

import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class Product {
    private String productName;
    private String price;
    private String pType;
    private String pDescricption;
    private String pAddInfo;
    private String pShopType;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private String currencySymbol;

    public Product() {
    }

    public Product(String productName, String price, String pType, String pDescricption, String pAddInfo, String pShopType, ArrayList<String> imagePaths, String currencySymbol) {
        this.productName = productName;
        this.price = price;
        this.pType = pType;
        this.pDescricption = pDescricption;
        this.pAddInfo = pAddInfo;
        this.pShopType = pShopType;
        this.imagePaths = imagePaths;
        this.currencySymbol = currencySymbol;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getpDescricption() {
        return pDescricption;
    }

    public void setpDescricption(String pDescricption) {
        this.pDescricption = pDescricption;
    }

    public String getpAddInfo() {
        return pAddInfo;
    }

    public void setpAddInfo(String pAddInfo) {
        this.pAddInfo = pAddInfo;
    }

    public String getpShopType() {
        return pShopType;
    }

    public void setpShopType(String pShopType) {
        this.pShopType = pShopType;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}
