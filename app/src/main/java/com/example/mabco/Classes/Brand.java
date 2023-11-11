package com.example.mabco.Classes;

import java.util.ArrayList;

public class Brand {
    String brand_name ,brand_code ,brand_image,brand_icon;
    ArrayList<Product> products ;


    public Brand(String brand_name, String brand_code, String brand_image, String brand_icon, ArrayList<Product> products) {
        this.brand_name = brand_name;
        this.brand_code = brand_code;
        this.brand_image = brand_image;
        this.brand_icon = brand_icon;
        this.products = products;
    }

    public Brand(String brand_name, String brand_code, String brand_image, ArrayList<Product> products) {
        this.brand_name = brand_name;
        this.brand_code = brand_code;
        this.brand_image = brand_image;
        this.products=products;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public String getBrand_image() {
        return brand_image;
    }

    public void setBrand_image(String brand_image) {
        this.brand_image = brand_image;
    }
    public String getBrand_icon() {
        return brand_icon;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
    public void setBrand_icon(String brand_icon) {
        this.brand_icon = brand_icon;
    }

}
