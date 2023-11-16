package com.example.mabco.Classes;

public class Offer {
    String offer_no ,offer_desc , discount , offer_title ,  offer_image_url;
    int offer_image;

    public String getOffer_image_url() {
        return offer_image_url;
    }

    public void setOffer_image_url(String offer_image_url) {
        this.offer_image_url = offer_image_url;
    }

    public Offer(String offer_no, String offer_desc, String offer_title, String offer_image_url) {
        this.offer_no = offer_no;
        this.offer_desc = offer_desc;
        this.offer_title = offer_title;
        this.offer_image_url = offer_image_url;
    }

    public Offer(String offer_title, int offer_image) {
        this.offer_title = offer_title;
        this.offer_image = offer_image;
    }
    public Offer(String offer_no, String offer_desc, String offer_title, int offer_image) {
        this.offer_no = offer_no;
        this.offer_desc = offer_desc;
        this.discount = discount;
        this.offer_title = offer_title;
        this.offer_image = offer_image;
    }

    public String getOffer_no() {
        return offer_no;
    }

    public void setOffer_no(String offer_no) {
        this.offer_no = offer_no;
    }

    public String getOffer_desc() {
        return offer_desc;
    }

    public void setOffer_desc(String offer_desc) {
        this.offer_desc = offer_desc;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOffer_title() {
        return offer_title;
    }

    public void setOffer_title(String offer_title) {
        this.offer_title = offer_title;
    }

    public int getOffer_image() {
        return offer_image;
    }

    public void setOffer_image(int offer_image) {
        this.offer_image = offer_image;
    }
}
