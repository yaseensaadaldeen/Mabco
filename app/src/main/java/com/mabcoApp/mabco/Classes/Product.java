package com.mabcoApp.mabco.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    String stk_code, stk_desc, shelf_price, discount, coupon, tag, product_title, product_image;
    CategoryModel categoryModel;
    Brand brand;
    ProductColor productColor;
    ArrayList<ProductSpecs> productSpecs;



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
    public Product(String stk_code, String product_title, String stk_desc, String shelf_price, CategoryModel categoryModel, String discount, String coupon, String tag, String product_image,    ArrayList<ProductSpecs> productSpecs) {
        this.stk_code = stk_code;
        this.stk_desc = stk_desc;
        this.shelf_price = shelf_price;
        this.discount = discount;
        this.coupon = coupon;
        this.tag = tag;
        this.product_title = product_title;
        this.product_image = product_image;
        this.categoryModel = categoryModel;
        this.productSpecs=productSpecs;
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

    private static final String PREF_NAME = "ProductCompare";
    private static final String PRODUCT1_KEY = "product1";
    private static final String PRODUCT2_KEY = "product2";

    // Save product to SharedPreferences and return number of stored products
    public static boolean AddProductToComparison(Context context, Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String product1Json = sharedPreferences.getString(PRODUCT1_KEY, null);
        String product2Json = sharedPreferences.getString(PRODUCT2_KEY, null);

        Product product1 = fromJson(product1Json);
        Product product2 = fromJson(product2Json);

        if ((product1 != null && product1.getStk_code().equals(product.getStk_code())) ||
                (product2 != null && product2.getStk_code().equals(product.getStk_code()))) {
            return false; // Product already exists
        }

        if (product1 == null) {
            editor.putString(PRODUCT1_KEY, product.toJson());
        } else if (product2 == null) {
            if (product.getCategoryModel().getCat_code().equals(product1.getCategoryModel().getCat_code())) {
                if (!product.getStk_code().equals(product1.getStk_code()))
                    editor.putString(PRODUCT2_KEY, product.toJson());
                else {
                    Toast.makeText(context, "Can't add same product twice", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(context, "Products must be from the same category", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(context, "Only 2 products can be compared at a time", Toast.LENGTH_SHORT).show();
            return false;
        }

        editor.apply();
        return true;
    }

    // Remove product from SharedPreferences and return number of stored products
    public static boolean RemoveProductFromComparison(Context context, Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String product1Json = sharedPreferences.getString(PRODUCT1_KEY, null);
        String product2Json = sharedPreferences.getString(PRODUCT2_KEY, null);

        Product product1 = fromJson(product1Json);
        Product product2 = fromJson(product2Json);

        if (product1 != null && product1.getStk_code().equals(product.getStk_code())) {
            if (product2 != null) {
                editor.putString(PRODUCT1_KEY, product2.toJson());
                editor.remove(PRODUCT2_KEY);
            } else {
                editor.remove(PRODUCT1_KEY);
            }
        } else if (product2 != null && product2.getStk_code().equals(product.getStk_code())) {
            editor.remove(PRODUCT2_KEY);
        } else {
            Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
        return true;
    }

    // Count the number of stored products
    public static int countComparedProducts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int count = 0;
        if (sharedPreferences.getString(PRODUCT1_KEY, null) != null) count++;
        if (sharedPreferences.getString(PRODUCT2_KEY, null) != null) count++;
        return count;
    }

    // Check if product exists in SharedPreferences
    public static boolean isProductInComparison(Context context, Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String product1Json = sharedPreferences.getString(PRODUCT1_KEY, null);
        String product2Json = sharedPreferences.getString(PRODUCT2_KEY, null);

        Product product1 = fromJson(product1Json);
        Product product2 = fromJson(product2Json);

        if ((product1 != null && product1.getStk_code().equals(product.getStk_code())) ||
                (product2 != null && product2.getStk_code().equals(product.getStk_code()))) {
            return true; // Product exists
        }

        return false;
    }
    public static ArrayList<Product> filterProducts(ArrayList<Product> products, double minPrice, double maxPrice, List<CategoryModel> categories) {
        ArrayList<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            // Parse the shelf price from String to double
            double shelfPrice = Double.parseDouble(product.getShelf_price());

            // Check if the product falls within the price range
            if (shelfPrice >= minPrice && shelfPrice <= maxPrice) {
                // Check if the product's category matches at least one category from the list
                if (product.getCategoryModel() != null) {
                    String productCategoryCode = product.getCategoryModel().getCat_code();

                    if (categories.size() > 0) {
                        // Iterate over the selected CategoryModel objects
                        for (CategoryModel category : categories) {
                            // Check if the category code matches
                            if (productCategoryCode.equals(category.getCat_code())) {
                                filteredProducts.add(product);
                                break; // Exit the loop if a match is found
                            }
                        }
                    } else {
                        // If no categories are provided, include the product
                        filteredProducts.add(product);
                    }
                }
            }
        }

        return filteredProducts;
    }

}
