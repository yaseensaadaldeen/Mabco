package com.example.mabco.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class Product implements Parcelable {
    String stk_code, stk_desc, shelf_price, discount, coupon, tag, product_title, product_image;
    CategoryModel categoryModel;
    Brand brand;
    ProductColor productColor;



    public Product(String stk_code, String stk_desc, String shelf_price, String discount, String coupon, String product_title) {
        this.stk_code = stk_code;
        this.stk_desc = stk_desc;
        this.shelf_price = shelf_price;
        this.discount = discount;
        this.coupon = coupon;
        this.product_title = product_title;
    }

    public Product(String stk_code, String product_title, String stk_desc, String shelf_price, CategoryModel categoryModel, String discount, String coupon, String tag, String product_image) {
        this.stk_code = stk_code;
        this.stk_desc = stk_desc;
        this.shelf_price = shelf_price;
        this.discount = discount;
        this.coupon = coupon;
        this.tag = tag;
        this.product_title = product_title;
        this.product_image = product_image;
        this.categoryModel = categoryModel;
    }

    public Product(String stk_code, String stk_desc, String shelf_price, String product_title, String discount, String coupon, String tag, CategoryModel categoryModel, Brand brand, String product_image) {
        this.stk_code = stk_code;
        this.product_title = product_title;
        this.stk_desc = stk_desc;
        this.shelf_price = shelf_price;
        this.discount = discount;
        this.coupon = coupon;
        this.tag = tag;
        this.categoryModel = categoryModel;
        this.brand = brand;
        this.product_image = product_image;
    }

    public Product() {}

    protected Product(Parcel in) {
        stk_code = in.readString();
        stk_desc = in.readString();
        shelf_price = in.readString();
        discount = in.readString();
        coupon = in.readString();
        product_title = in.readString();
        product_image = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getStk_code() {
        return stk_code;
    }

    public void setStk_code(String stk_code) {
        this.stk_code = stk_code;
    }
    public String getProduct_title() {
        return product_title;
    }
    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getStk_desc() {
        return stk_desc;
    }

    public void setStk_desc(String stk_desc) {
        this.stk_desc = stk_desc;
    }

    public String getShelf_price() {
        return shelf_price;
    }

    public void setShelf_price(String shelf_price) {
        this.shelf_price = shelf_price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public ProductColor getProductColor() {
        return productColor;
    }

    public void setProductColor(ProductColor productColor) {
        this.productColor = productColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.stk_code, this.stk_desc, this.shelf_price, this.discount, this.coupon, this.tag, this.product_title, this.product_image});
    }
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Create a Product object from a JSON string
    public static Product fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Product.class);
    }
    public boolean matchesSearchText(String searchText) {
        // Convert both product title and description to lowercase for case-insensitive search
        String lowercaseSearchText = searchText.toLowerCase();
        String lowercaseProductName = product_title.toLowerCase();
        String lowercaseProductDescription = stk_desc.toLowerCase();

        // Check if either product name or description contains the search text
        return lowercaseProductName.contains(lowercaseSearchText) || lowercaseProductDescription.contains(lowercaseSearchText);
    }
}
