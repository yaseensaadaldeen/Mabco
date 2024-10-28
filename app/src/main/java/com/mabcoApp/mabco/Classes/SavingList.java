package com.mabcoApp.mabco.Classes;

public class SavingList {
    String slide_link , image_link,type,name;

    public SavingList(String slide_link, String image_link, String type,String name) {
        this.slide_link = slide_link;
        this.image_link = image_link;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSlide_link() {
        return slide_link;
    }

    public void setSlide_link(String slide_link) {
        this.slide_link = slide_link;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setName(String name) {
        this.type = name;
    }
}
