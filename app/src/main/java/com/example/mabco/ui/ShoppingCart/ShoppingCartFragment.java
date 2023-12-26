package com.example.mabco.ui.ShoppingCart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mabco.Adapters.ShoppingCartItemsAdapter;
import com.example.mabco.Classes.NetworkStatus;
import com.example.mabco.Classes.Product;
import com.example.mabco.Classes.ShoppingCart;
import com.example.mabco.MainActivity;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartFragment extends Fragment {
    View view;
    Context context;
    RecyclerView ShoppingcartItemsRecycle;
    ShoppingCartItemsAdapter shoppingCartItemsAdapter;
    TextView Total_price, TotalDiscount, FinalPrice;
    SharedPreferences ShoppingcartData, Userdata;
    LinearLayout Submit_Purchase_Order_btn;
    public RequestQueue requestQueue;

    public ShoppingCartFragment() {
    }

    public ShoppingCartFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        ShoppingcartData = context.getSharedPreferences("ShoppingCartData", Context.MODE_PRIVATE);
        Userdata = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);

        ShoppingcartItemsRecycle = view.findViewById(R.id.ShoppingcartItemsRecycle);
        Total_price = view.findViewById(R.id.txt_total_val);
        TotalDiscount = view.findViewById(R.id.total_discount_val);
        FinalPrice = view.findViewById(R.id.txt_final_price);
        Submit_Purchase_Order_btn = view.findViewById(R.id.Submit_Purchase_Order_btn);
        Resources standardResources = context.getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());
        if (NetworkStatus.isOnline(context) && ShoppingCart.isExpired(context))
        {
            UpdateProductsPrices(NetworkStatus.isOnline(context));
        }
        if (config.getLayoutDirection() == Layout.DIR_LEFT_TO_RIGHT) {
            Submit_Purchase_Order_btn.setBackground(this.getResources().getDrawable(R.drawable.shopping_cart_btn_ltr));
        }
        GetItemsFromPref();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void GetItemsFromPref() {
        List<Product> products = ShoppingCart.getProducts(context);
        products = ShoppingCart.removeDuplicates(products);
        shoppingCartItemsAdapter = new ShoppingCartItemsAdapter(products, context, ((MainActivity) getActivity()), view);
        ShoppingcartItemsRecycle.setAdapter(shoppingCartItemsAdapter);
        shoppingCartItemsAdapter.notifyDataSetChanged();
    }

    public void UpdatePricesSet(String TotalPrice, String Total_Discount, String Final_Price, View view) {
        Total_price = view.findViewById(R.id.txt_total_val);
        TotalDiscount = view.findViewById(R.id.total_discount_val);
        FinalPrice = view.findViewById(R.id.txt_final_price);
        Total_price.setText(TotalPrice);
        TotalDiscount.setText(Total_Discount);
        FinalPrice.setText(Final_Price);
    }

    //if the shopping cart has expired then we get the prices from the server
    public void UpdateProductsPrices(boolean online) {
        if (ShoppingCart.isExpired(context)) {
            String AllProducts = ShoppingCart.GetAllProducts(context);

            if (online) {
                requestQueue = Volley.newRequestQueue(context);
                com.example.mabco.HttpsTrustManager.allowAllSSL();
                String url = UrlEndPoint.General + "Service1.svc/GetProductsPrices/" + AllProducts;
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = jsonResponse.optJSONArray("GetProductsPricesResult");
                            if (array != null) {


                                String stk_code, coupon, discount, shelf_price;
                                ArrayList<String> question = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject arrayObj = array.getJSONObject(i);
                                    stk_code = arrayObj.optString("stk_code");
                                    shelf_price = arrayObj.optString("shelf_price");
                                    discount = arrayObj.optString("discount");
                                    if (arrayObj.optString("offer_spec").contains("on all invoice")) {
                                        coupon = arrayObj.optString("discount");
                                    } else
                                        coupon = "0";

                                    ShoppingCart.UpdatePrices(context, stk_code, shelf_price, discount, coupon);

                                }
                                GetItemsFromPref();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                            }
                        }) {
                };
                try {
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            15000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                    );
                    requestQueue.add(jsonObjectRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }
}