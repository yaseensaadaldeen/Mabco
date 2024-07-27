package com.example.mabco.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductSpecs implements Parcelable {
    String spec_title, spec_desc, spec_image;

    public ProductSpecs(String spec_title, String spec_desc, String spec_image) {
        this.spec_title = spec_title;
        this.spec_desc = spec_desc;
        this.spec_image = spec_image;
    }

    protected ProductSpecs(Parcel in) {
        spec_title = in.readString();
        spec_desc = in.readString();
        spec_image = in.readString();
    }

    public static final Creator<ProductSpecs> CREATOR = new Creator<ProductSpecs>() {
        @Override
        public ProductSpecs createFromParcel(Parcel in) {
            return new ProductSpecs(in);
        }

        @Override
        public ProductSpecs[] newArray(int size) {
            return new ProductSpecs[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(spec_title);
        dest.writeString(spec_desc);
        dest.writeString(spec_image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getSpec_title() {
        return spec_title;
    }

    public void setSpec_title(String spec_title) {
        this.spec_title = spec_title;
    }

    public String getSpec_desc() {
        return spec_desc;
    }

    public void setSpec_desc(String spec_desc) {
        this.spec_desc = spec_desc;
    }

    public String getSpec_image() {
        return spec_image;
    }

    public void setSpec_image(String spec_image) {
        this.spec_image = spec_image;
    }
}
