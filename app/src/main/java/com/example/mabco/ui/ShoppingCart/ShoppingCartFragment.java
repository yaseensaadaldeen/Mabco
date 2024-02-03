package com.example.mabco.ui.ShoppingCart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
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
import com.example.mabco.HttpsTrustManager;
import com.example.mabco.MainActivity;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

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
    NavController navController;
    private RequestQueue queue;
    private MainActivity mainActivity;

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
        show();
        ShoppingcartItemsRecycle = view.findViewById(R.id.ShoppingcartItemsRecycle);
        Total_price = view.findViewById(R.id.txt_total_val);
        TotalDiscount = view.findViewById(R.id.total_discount_val);
        FinalPrice = view.findViewById(R.id.txt_final_price);
        Submit_Purchase_Order_btn = view.findViewById(R.id.Submit_Purchase_Order_btn);
        Resources standardResources = context.getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        queue = Volley.newRequestQueue(context);
        Configuration config = new Configuration(standardResources.getConfiguration());
        if (NetworkStatus.isOnline(context) && ShoppingCart.isExpired(context)) {
            UpdateProductsPrices(NetworkStatus.isOnline(context));
        }
        if (config.getLayoutDirection() == Layout.DIR_LEFT_TO_RIGHT) {
            Submit_Purchase_Order_btn.setBackground(this.getResources().getDrawable(R.drawable.shopping_cart_btn_ltr));
        }
        if (ShoppingCart.getItemCount(context) == 0)
            Toast.makeText(context, context.getResources().getString(R.string.submit_empty), Toast.LENGTH_LONG).show();

        Submit_Purchase_Order_btn.setOnClickListener(v -> {
            if (ShoppingCart.getItemCount(context) > 0) {
                boolean UserSigninCheck = Userdata.getBoolean("Verified", false);
                if (UserSigninCheck) {
                    Snackbar.make(view, R.string.sure_buy, Snackbar.LENGTH_LONG)
                            .setAction(R.string.yes, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showInput(context.getResources().getString(R.string.input_address));
                                }
                            }).show();

                } else {
                    navController = Navigation.findNavController(view);
                    navController.navigate((NavDirections) ShoppingCartFragmentDirections.actionShoppingCartFragmentToSignInMain());
                }
            } else
                Toast.makeText(context, context.getResources().getString(R.string.submit_empty), Toast.LENGTH_LONG).show();

        });
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
    public void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.INVISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    public void show() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }

    private void showInput(final String msg) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final ProgressBar progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.GONE);
        final TextInputLayout textLayout = new TextInputLayout(context);
        textLayout.setHint(msg);
        final EditText editText = new EditText(context);
//        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        textLayout.addView(editText);
        linearLayout.addView(progressBar);
        linearLayout.addView(textLayout);


        final AlertDialog createdAlert = showMessageDialog(context, msg, getString(R.string.out_time_to_buy), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }, linearLayout, false);
        createdAlert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button button = createdAlert.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Dismiss once everything is OK.
                        String textAddress = editText.getText().toString().trim();
                        if (textAddress.isEmpty()) {
                            editText.setError(msg);
                            editText.requestFocus();
                            return;
                        }
                        submitOnline(textAddress, createdAlert, progressBar, textLayout);
                    }
                });
            }
        });
        createdAlert.show();
    }

    private void submitOnline(final String textAddress, final AlertDialog createdAlert, final ProgressBar progressBar, final TextInputLayout textLayout) {
        //hide alert when done
        StringBuilder prices = new StringBuilder(), stocks = new StringBuilder();

        List<Product> shoppingcartProducts = ShoppingCart.getProducts(context);

        for (Product listViewItem : shoppingcartProducts) {
            prices.append(listViewItem.getShelf_price()).append(";");
            stocks.append(listViewItem.getStk_code()).append(";");
        }


        progressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/buy/" + stocks
                + "," + prices.toString().replace(",", "")
                + "," + Userdata.getString("PhoneNO", "empty")
                + "," + Userdata.getString("UserName", "empty")
                + "," + textAddress;
        StringRequest strRequest1 = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray jsonArray;

                            String res = jsonResponse.getString("buyResult");

                            if (res.equals("1")) {
                                textLayout.setVisibility(View.GONE);
                                createdAlert.setTitle(getString(R.string.done));
                                createdAlert.setMessage(getString(R.string.call_back_soon));
                                Button b = createdAlert.getButton(AlertDialog.BUTTON_POSITIVE);
                                b.setText(context.getResources().getString(R.string.ok));
                                b.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ShoppingCart.emptyShoppingCart(context);
                                        GetItemsFromPref();
                                        UpdatePricesSet(ShoppingCart.GetTotalPrice(context), ShoppingCart.GetTotalDiscount(context), ShoppingCart.GetFinalPrice(context), view);
                                        int shoppingcartItems = ShoppingCart.getItemCount(context);
                                        ((MainActivity) requireActivity()).updateNotificationCount(shoppingcartItems);
                                        createdAlert.hide();
                                    }
                                });

                            } else {
                                //error
                                textLayout.setError(context.getResources().getString(R.string.try_again));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            textLayout.setError(context.getResources().getString(R.string.try_again));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, context.getResources().getString(R.string.checkwifi), Toast.LENGTH_LONG).show();
                        textLayout.setError(context.getResources().getString(R.string.try_again));
                    }
                });

        strRequest1.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );
        queue.add(strRequest1);
    }

    AlertDialog showMessageDialog(Context context, String title, String message, DialogInterface.OnClickListener DialogInterface, DialogInterface.OnClickListener cancelDialogInterface, View view, boolean isShow) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.mabco);

        alertDialog.setPositiveButton(context.getResources().getString(R.string.submit), DialogInterface);
        alertDialog.setCancelable(false);

        if (view != null) {
            alertDialog.setView(view);
        }

        if (cancelDialogInterface != null)
            alertDialog.setNegativeButton(context.getResources().getString(R.string.cancel), cancelDialogInterface);

        final AlertDialog alert = alertDialog.create();


        if (!((Activity) context).isFinishing() && isShow)//to fix exception  ; is your activity running?
            alert.show();


        //dismiss when press back button
        alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alert.getButton(android.content.DialogInterface.BUTTON_NEGATIVE).callOnClick();
                    return true;
                }
                return false;
            }
        });


        return alert;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}