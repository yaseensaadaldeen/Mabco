package com.mabcoApp.mabco.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryModel implements Parcelable {
    String title ,cat_code ;
    int image;
    protected CategoryModel(Parcel in) {
        title = in.readString();
        cat_code = in.readString();
        image = in.readInt();
    }

    public static final Creator<CategoryModel> CREATOR = new Creator<CategoryModel>() {
        @Override
        public CategoryModel createFromParcel(Parcel in) {
            return new CategoryModel(in);
        }

        @Override
        public CategoryModel[] newArray(int size) {
            return new CategoryModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(cat_code);
        dest.writeInt(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public CategoryModel(String title, String cat_code, int image) {
        this.title = title;
        this.cat_code = cat_code;
        this.image = image;
    }

    public CategoryModel(String cat_code) {
        this.cat_code = cat_code;
    }

    public CategoryModel(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }
}
