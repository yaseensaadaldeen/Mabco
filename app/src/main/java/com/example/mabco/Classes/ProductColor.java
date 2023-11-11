package com.example.mabco.Classes;

public class ProductColor {
    String  color_name, color_code , image_Link;

    public ProductColor(String color_name, String color_code, String image_Link) {
        this.color_name = color_name;
        this.color_code = color_code;
        this.image_Link = image_Link;
    }

    public ProductColor() {

    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getImage_Link() {
        return image_Link;
    }

    public void setImage_Link(String image_Link) {
        this.image_Link = image_Link;
    }
}
