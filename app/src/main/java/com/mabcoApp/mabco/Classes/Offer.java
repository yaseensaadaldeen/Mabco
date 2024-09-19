package com.mabcoApp.mabco.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Offer implements Parcelable {
    String offer_no, offer_desc, discount, offer_title, offer_image_url;
    int offer_image;

    public Offer() {
    }

    // Constructor with parcel
    protected Offer(Parcel in) {
        offer_no = in.readString();
        offer_desc = in.readString();
        discount = in.readString();
        offer_title = in.readString();
        offer_image_url = in.readString();
        offer_image = in.readInt();
    }

    // Parcelable CREATOR
    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    // Parcelable required method
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(offer_no);
        dest.writeString(offer_desc);
        dest.writeString(discount);
        dest.writeString(offer_title);
        dest.writeString(offer_image_url);
        dest.writeInt(offer_image);
    }

    // Required method for parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
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
