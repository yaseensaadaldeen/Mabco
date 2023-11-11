package com.example.mabco.Classes;

import androidx.recyclerview.widget.RecyclerView;

public class CategoryModel {
    String title ,cat_code ;
    int image;

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
