package com.example.mabco.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ShoppingCart implements Parcelable {
    private static final String PREF_NAME = "ShoppingCartData";
    private static final String PRODUCTS_KEY = "products";
    private static final String ITEM_COUNT_KEY = "items_count";
    private static final String CREATED_DATE_KEY = "cart_created_date";
    Product[] products;
    String status;

    protected ShoppingCart(Parcel in) {
        products = in.createTypedArray(Product.CREATOR);
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(products, flags);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShoppingCart> CREATOR = new Creator<ShoppingCart>() {
        @Override
        public ShoppingCart createFromParcel(Parcel in) {
            return new ShoppingCart(in);
        }

        @Override
        public ShoppingCart[] newArray(int size) {
            return new ShoppingCart[size];
        }
    };

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Save the products List and item count to SharedPreferences
    public static void saveShoppingCart(Context context, List<Product> products, int itemCount, Date createdDate) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Convert the List of Product objects to a List of JSON strings
        List<String> productJsonStrings = new ArrayList<>();
        for (Product product : products) {
            productJsonStrings.add(product.toJson());
        }
        if (getCreatedDate(context) == null && createdDate != null) {
            editor.putLong(CREATED_DATE_KEY, createdDate.getTime());
        }
        editor.putString(PRODUCTS_KEY, new Gson().toJson(productJsonStrings));
        editor.putInt(ITEM_COUNT_KEY, itemCount);

        // Apply the changes synchronously
        editor.apply();
    }

    // Retrieve the products List from SharedPreferences
    public static List<Product> getProducts(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        List<String> productJsonStrings = new Gson().fromJson(preferences.getString(PRODUCTS_KEY, "[]"), ArrayList.class);

        List<Product> products = new ArrayList<>();

        // Convert the list of JSON strings back to a list of Product objects
        for (String jsonString : productJsonStrings) {
            products.add(Product.fromJson(jsonString));
        }

        return products;
    }

    // Retrieve the item count from SharedPreferences
    public static int getItemCount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Default value is 0
        return preferences.getInt(ITEM_COUNT_KEY, 0);
    }

    public static int getProductItemsCount(Context context, Product product) {
        int ProductItemsCount = 0;
        List<Product> productList = getProducts(context);
        for (Product existingProduct : productList) {
            if (existingProduct.stk_code.equals(product.getStk_code())) {
                ProductItemsCount = ProductItemsCount + 1;
            }
        }
        return ProductItemsCount;
    }

    // Add a product to the shopping cart
    public static boolean addToCart(Context context, Product product) {
        List<Product> products = getProducts(context);

        boolean notcontainsProduct = true;
        // Check if the mobile product entered before (company policy)
        int sameproductcount = 0;
        for (Product existingProduct : products) {
            if (existingProduct.categoryModel.getCat_code().equals("00")) {
                if (existingProduct.stk_code.equals(product.stk_code)) {
                    notcontainsProduct = false;
                    break;
                }
            } else {
                // Check if the same product entered more than 5 times (company policy)
                if (existingProduct.stk_code.equals(product.stk_code)) {
                    sameproductcount = sameproductcount + 1;
                    if (sameproductcount >= 5) {
                        notcontainsProduct = false;
                        break;
                    }
                }
            }
        }
        Date createdDate = getCreatedDate(context);
        if (notcontainsProduct) {
            if (products.isEmpty()) {
                createdDate = new Date(System.currentTimeMillis());
            }
            products.add(product);

            // Increment the item count
            int itemCount = getItemCount(context) + 1;

            // Save the updated shopping cart
            saveShoppingCart(context, products, itemCount, createdDate);
        }
        return notcontainsProduct;
    }

    // Remove a product from the shopping cart
    public static void removeFromCart(Context context, Product product) {
        List<Product> products = getProducts(context);
        for (Product thisproduct : products) {
            if (thisproduct.getStk_code().equals(product.getStk_code())) {
                products.remove(thisproduct);
                break;
            }
        }
        // Decrement the item count, ensuring it doesn't go below 0
        int itemCount = Math.max(0, getItemCount(context) - 1);
        if (products.isEmpty()) {
            clearCreatedDate(context);
        }
        // Save the updated shopping cart
        saveShoppingCart(context, products, itemCount, getCreatedDate(context));
    }

    public static void removeAllFromCart(Context context, Product removing_products) {
        try {
            List<Product> products = getProducts(context);
            int itemCount = Math.max(0, getItemCount(context) );
            Iterator<Product> iterator = products.iterator();
            while (iterator.hasNext()) {
                Product product = iterator.next();
                if (product.getStk_code().equals(removing_products.getStk_code())) {
                    // Remove the current element using the iterator's remove method
                    iterator.remove();
                    // Decrement the item count, ensuring it doesn't go below 0
                     itemCount = Math.max(0, itemCount - 1);
                }
            }
            if (products.isEmpty()) {
                clearCreatedDate(context);
            }
            // Save the updated shopping cart
            saveShoppingCart(context, products, itemCount, getCreatedDate(context));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getCreatedDate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        long createdDateMillis = preferences.getLong(CREATED_DATE_KEY, 0);

        // Return null if the created date is not set
        return (createdDateMillis > 0) ? new Date(createdDateMillis) : null;
    }

    // Clear the created date from SharedPreferences
    public static void clearCreatedDate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(CREATED_DATE_KEY);
        editor.apply();
    }

    // Check if the created date has expired
    public static boolean isExpired(Context context) {
        Date createdDate = getCreatedDate(context);

        if (createdDate != null) {
            // Calculate the current time
            Date currentTime = new Date(System.currentTimeMillis());

            // Calculate the time difference in milliseconds
            long timeDifference = currentTime.getTime() - createdDate.getTime();

            // Check if the time difference is greater than 20 minutes (20 * 60 * 1000 milliseconds)
            return timeDifference > (20 * 60 * 1000);
        }

        // If there is no created date, it is considered expired
        return true;
    }

    public static void emptyShoppingCart(Context context) {
        clearCreatedDate(context);
        saveShoppingCart(context, new ArrayList<>(), 0, null);
    }
    public static List<Product> removeDuplicates(List<Product> originalList) {
        Set<String> uniqueNames = new HashSet<>();
        List<Product> uniqueList = new ArrayList<>();

        for (Product obj : originalList) {
            // Check if the name is unique
            if (uniqueNames.add(obj.getStk_code())) {
                uniqueList.add(obj);
            }
        }

        return uniqueList;
    }
    public static String GetTotalPrice(Context context)
    {

        try {
            List<Product> products = getProducts(context);
            Long TotalPrice = new Long(0);

            for (Product product : products) {
                Long shelt_price = Long.valueOf(product.getShelf_price().replace(",",""));
                TotalPrice = TotalPrice + shelt_price;
            }
            return TotalPrice +" SP";
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
    public static String GetTotalDiscount(Context context)
    {
        try {
            List<Product> products = getProducts(context);
            Long TotalDiscount = new Long(0);

            for (Product product : products) {
                TotalDiscount = TotalDiscount + (Long.parseLong(product.getDiscount()));
            }
            return TotalDiscount +" SP";
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
    public static  String GetFinalPrice(Context context) {
        try {
            List<Product> products = getProducts(context);
            Long final_price = new Long(0);

            for (Product product : products) {
                Long shelt_price = Long.valueOf(product.getShelf_price().replace(",",""));
                final_price = final_price + (shelt_price - (Long.parseLong(product.getDiscount())));
            }
            return final_price +" SP";
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
    public static String GetAllProducts(Context context){
        List<Product> originalList = getProducts(context);
        String Stk_codes = "''";
        Set<String> uniqueNames = new HashSet<>();
        List<Product> uniqueList = new ArrayList<>();
        for (Product obj : originalList) {
            Stk_codes = Stk_codes + "'',''" + obj .getStk_code();
        }
        return Stk_codes +"''";
    }
    public static  void UpdatePrices (Context context , String stk_code , String Shelf_price , String discount,String coupon)
    {
        List<Product> products = getProducts(context);
        for (Product thisproduct : products) {
            if (thisproduct.getStk_code().equals(stk_code)) {
                thisproduct.setShelf_price(Shelf_price);
                thisproduct.setDiscount(discount);
                thisproduct.setCoupon(coupon);
            }
        }
        saveShoppingCart(context, products, getItemCount(context), getCreatedDate(context));
    }
}
