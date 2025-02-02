package com.mabcoApp.mabco.ui.Offers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mabcoApp.mabco.Adapters.ProductsAdapter;
import com.mabcoApp.mabco.Classes.CategoryModel;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.SavingList;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.ui.Home.HomeFragmentDirections;
import com.mabcoApp.mabco.ui.Product.ProductDetailsFragmentDirections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class OfferProductDialog extends Dialog {
    private ArrayList<Product> offerproducts;
    private ProductsAdapter productsAdapter;
    private Context context;
    public RequestQueue requestQueue;
    NavController navController;
    SharedPreferences preferences, PersonalPreference;
    String local;
    private Offer offer;
    public String offer_no;
    private View view;
    TextView offer_desc;
    FloatingActionButton btnCancel;
    String from, stk_code;
    private RecyclerView OfferProductsRecyler;
    public ShimmerFrameLayout OffershimmerViewContainer;
    SavingList savingList;

    public OfferProductDialog(@NonNull Context context, Offer offer, NavController navController, String from) {
        super(context);
        this.offer = offer;
        this.navController = navController;
        this.from = from;
    }

    public OfferProductDialog(@NonNull Context context, SavingList savingList, NavController navController, String from) {
        super(context);
        this.savingList = savingList;
        this.navController = navController;
        this.from = from;
    }

    public OfferProductDialog(@NonNull Context context, Offer offer, NavController navController, String from, String stk_code) {
        super(context);
        this.offer = offer;
        this.navController = navController;
        this.from = from;
        this.stk_code = stk_code;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState != null ? savedInstanceState : new Bundle());

        view = LayoutInflater.from(getContext()).inflate(R.layout.offer_products_dialog, null);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> cancel());
        context = view.getContext();
        preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");
        offer_desc = view.findViewById(R.id.offer_desc);

        if (offer != null) {
            offer_no = offer.getOffer_no();
            offer_desc.setText(offer.getOffer_desc());
        } else {
            offer_desc.setText(savingList.getName().isEmpty()?"قوائم التوفير":savingList.getName());
        }

        OffershimmerViewContainer = view.findViewById(R.id.offer_shimmer_view_containers);
        OffershimmerViewContainer.startShimmer();
        setContentView(view);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        OfferProductsRecyler = view.findViewById(R.id.offer_products);
        OfferProductsRecyler.setLayoutManager(new LinearLayoutManager(getContext()));
        if (savingList == null) {
            if (savedInstanceState == null) OfferProductsAPI(context, haveNetworkConnection());
            else OfferProductsAPI(context, false);
        } else {
            if (savedInstanceState == null) SavingListProductsAPI(context, haveNetworkConnection());
            else SavingListProductsAPI(context, false);
        }
    }

    public void OfferProductsAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
            String UserID = Token.getString("UserID", "");
            String url = UrlEndPoint.General + "Service1.svc/getstocksbyoffer/" + offer_no + ",app," + UserID;
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("OfferProducts" + offer_no, response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("getOfferProductsResult");
                        if (array != null) {
                            LoadProducts(array);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    OfferProductsAPI(context, false);
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("X-Content-Type-Options", "nosniff");
                    params.put("X-XSS-Protection", "0");
                    params.put("X-Frame-Options", "DENY");
                    //..add other headers
                    return params;
                }
            };
            try {
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String ProductsWithDiscount = preferences.getString("OfferProducts" + offer_no, "");
            if (!ProductsWithDiscount.equals("")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(ProductsWithDiscount);
                    JSONArray array = jsonResponse.optJSONArray("getOfferProductsResult");
                    if (array != null) {
                        LoadProducts(array);

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("No items to show");
                alertDialog.setTitle("Sorry");
                alertDialog.setPositiveButton("OK", (dialog, which) -> {
                    cancel();
                    btnCancel.callOnClick();
                });
                alertDialog.setCancelable(true);
                alertDialog.create().show();
            }
        }
    }

    public void SavingListProductsAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
            String UserID = Token.getString("UserID", "");
            String url = UrlEndPoint.General + "Service1.svc/getSavingList/" + savingList.getType()+ ","+ UserID;
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("SavingListProducts" + savingList.getType(), response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("GetSavingListResult");
                        if (array != null) {
                            LoadProducts(array);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    OfferProductsAPI(context, false);
                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("X-Content-Type-Options", "nosniff");
                    params.put("X-XSS-Protection", "0");
                    params.put("X-Frame-Options", "DENY");
                    //..add other headers
                    return params;
                }
            };
            try {
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String ProductsWithDiscount = preferences.getString("SavingListProducts" + savingList.getType(), "");
            if (!ProductsWithDiscount.equals("")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(ProductsWithDiscount);
                    JSONArray array = jsonResponse.optJSONArray("SavingListProducts");
                    if (array != null) {
                        LoadProducts(array);

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("No items to show");
                alertDialog.setTitle("Sorry");
                alertDialog.setPositiveButton("OK", (dialog, which) -> {
                    cancel();
                    btnCancel.callOnClick();
                });
                alertDialog.setCancelable(true);
                alertDialog.create().show();
            }
        }
    }

    private void LoadProducts(JSONArray array) {
        try {

            offerproducts = new ArrayList<Product>();
            try {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject arrayObj = array.getJSONObject(i);
                    if (arrayObj.optString("stk_code").equals("")) continue;
                    String coupon = "0";
                    if (arrayObj.optString("offer_spec").contains("on all invoice")) {
                        coupon = arrayObj.optString("discount");
                    }
                    Product product = new Product(arrayObj.optString("stk_code"), arrayObj.optString("device_title"), arrayObj.optString("stk_desc"), arrayObj.optString("shelf_price"), new CategoryModel(arrayObj.optString("cat_code")), arrayObj.optString("discount"), arrayObj.optString("coupon"), "offer", "https://mabcoonline.com/" + arrayObj.optString("image_link"));

                    offerproducts.add(product);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (offerproducts.size() == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setMessage("No items to show");
            alertDialog.setTitle("Sorry");
            alertDialog.setPositiveButton("OK", (dialog, which) -> {
                cancel();
                btnCancel.callOnClick();});
            alertDialog.setCancelable(true);
            alertDialog.create().show();

        } else {
            OffershimmerViewContainer.stopShimmer();
            OffershimmerViewContainer.setVisibility(View.GONE);
            productsAdapter = new ProductsAdapter(context, offerproducts, local);
            OfferProductsRecyler.setAdapter(productsAdapter);//.notifyDataSetChanged();
            productsAdapter.setOnClickListener(new ProductsAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Product product) {//there where i want to navigate to the productdetalesfragment passing the product to it

                    try {
                        if (from.equals("offerFragment"))
                            navController.navigate((NavDirections) OffersFragmentDirections.actionProductOfferFragmentToProductDetailsFragment(product));
                        else if (from.equals("Home"))
                            navController.navigate((NavDirections) HomeFragmentDirections.actionNavHomeToProductDetailsFragment(product));
                        else {
                            if (stk_code.equals(product.getStk_code())) {
                                Toast.makeText(context, "المنتج ذاته !!", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else
                                navController.navigate((NavDirections) ProductDetailsFragmentDirections.actionProductDetailsFragmentSelf(product));
                        }
                        dismiss();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            productsAdapter.notifyDataSetChanged();
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE.toString());
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}

