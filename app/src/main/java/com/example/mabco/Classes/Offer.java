package com.example.mabco.Classes;

public class Offer {
    String offer_no ,offer_desc , discount , offer_title;
    int offer_image;

    public Offer(String offer_title, int offer_image) {
        this.offer_title = offer_title;
        this.offer_image = offer_image;
    }
    public Offer(String offer_no, String offer_desc, String discount, String offer_title, int offer_image) {
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

    public int getOffer_image() {
        return offer_image;
    }

    public void setOffer_image(int offer_image) {
        this.offer_image = offer_image;
    }
}
