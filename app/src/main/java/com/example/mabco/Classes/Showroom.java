package com.example.mabco.Classes;

public class Showroom {
    String loc_code, loc_name, image_link, phone_no, city , loc_desc;

    public Showroom(String loc_code, String loc_name, String image_link, String phone_no, String city, String loc_desc) {
        this.loc_code = loc_code;
        this.loc_name = loc_name;
        this.image_link = image_link;
        this.phone_no = phone_no;
        this.city = city;
        this.loc_desc = loc_desc;
    }

    public String getLoc_desc() {
        return loc_desc;
    }

    public void setLoc_desc(String loc_desc) {
        this.loc_desc = loc_desc;
    }

    public String getLoc_code() {
        return loc_code;
    }

    public void setLoc_code(String loc_code) {
        this.loc_code = loc_code;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
