package com.example.mabco.ui.Product;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mabco.Adapters.ProductColorAdapter;
import com.example.mabco.Adapters.ProductDetailsAdapter;
import com.example.mabco.Adapters.ProductSpecsAdapter;
import com.example.mabco.Classes.Product;
import com.example.mabco.Classes.ProductColor;
import com.example.mabco.Classes.Offer;
import com.example.mabco.Classes.ProductSpecs;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;
import com.example.mabco.databinding.FragmentProductDetailesBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ProductDetailsFragment extends Fragment {
    private FragmentProductDetailesBinding Detailesbinding;
    private RequestQueue requestQueue;
    public Context context;
    public RecyclerView ColorsRecycleview;
    public ArrayList<ProductSpecs> productSpecs;
    public ArrayList<ProductColor> productColors;
    public ArrayList<Offer> productOffers;
    public ProductColorAdapter productColorAdapter;
    public ImageView product_image;
    public ImageSlider product_images;
    public TextView product_name, product_price, product_disc, product_main_name;
    public Product product;
    public ArrayList<SlideModel> productSlide = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ProductDetailsAdapter productDetailsAdapter;
    private FloatingActionButton backbtn;
    private RelativeLayout prod_det;
    private LinearLayout add_to_shopping_btn;
    private MaterialCardView pro_img;
    SharedPreferences preferences;
    View view;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Detailesbinding = FragmentProductDetailesBinding.inflate(inflater, container, false);

            view = inflater.inflate(R.layout.fragment_product_detailes, container, false);
            context = getContext();
            assert context != null;
            preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
            hide();
            ProductDetailsFragmentArgs args = ProductDetailsFragmentArgs.fromBundle(getArguments());
            product = args.getProduct();
            prod_det = Detailesbinding.prodDet;
            backbtn = Detailesbinding.backBtn;
            backbtn.setOnClickListener(v -> getActivity().onBackPressed());
            product_main_name = Detailesbinding.productMainName;
            pro_img = Detailesbinding.proImg;
            product_images = Detailesbinding.productImages;
            ColorsRecycleview = Detailesbinding.productColors;
            product_name = Detailesbinding.productName;
            add_to_shopping_btn = Detailesbinding.addToShoppingBtn;
            productDetailsAdapter = new ProductDetailsAdapter(this);
            Resources standardResources = context.getResources();
            AssetManager assets = standardResources.getAssets();
            DisplayMetrics metrics = standardResources.getDisplayMetrics();
            Configuration config = new Configuration(standardResources.getConfiguration());
            if (config.getLayoutDirection() == Layout.DIR_LEFT_TO_RIGHT) {
                backbtn.setImageResource(R.drawable.back_rtl);
                add_to_shopping_btn.setBackground(this.getResources().getDrawable(R.drawable.shopping_cart_btn_ltr));
            }
            product_price = Detailesbinding.productPrice;
            product_disc = Detailesbinding.productDisc;
            product_image = Detailesbinding.productImage;
            product_name.setText(product.getProduct_title());
            product_price.setText(product.getShelf_price() + " SP");
            Glide.with(context).load(R.drawable.loading).centerCrop().into(product_image);
            product_image.setHorizontalFadingEdgeEnabled(true);
            product_image.setVerticalFadingEdgeEnabled(true);
            product_image.setFadingEdgeLength(40);
            if (!product.getDiscount().equals("0")) {
                product_disc.setVisibility(View.VISIBLE);
                if (product.getCoupon().equals("0")) {
                    product_disc.setText(String.valueOf((Long.parseLong(product.getShelf_price()))) + " SP");
                    String final_price = String.valueOf((Long.parseLong(product.getShelf_price()) - (Long.parseLong(product.getDiscount()))));
                    product_price.setText(final_price + " SP");
                    product_disc.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    product_disc.setTextColor(Color.RED);
                } else {
                    product_disc.setPaintFlags(product_price.getPaintFlags());
                    product_disc.setTextColor(Color.parseColor("#af0491cf"));
                    product_disc.setText(" قسيمة شرائية بقيمة" + Long.parseLong(product.getDiscount()) + " ل.س ");
                }
            }
            tabLayout = Detailesbinding.tabLayout;
            viewPager2 = Detailesbinding.pager;
            boolean online = haveNetworkConnection();
            ProductDetailsAPI(context, online);
            viewPager2.setAdapter(productDetailsAdapter);
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.details)), 0);

            if (product.getCategoryModel().getCat_code().equals("09"))
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.FAQ)), 1);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                  viewPager2.setCurrentItem(tab.getPosition());
                   // productDetailsAdapter.createFragment(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Detailesbinding.getRoot();
    }

    public void ProductDetailsAPI(Context context, boolean online) {
        requestQueue = Volley.newRequestQueue(context);
        com.example.mabco.HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/getStockDetails/" + product.getStk_code() + ",AR";
        if (online) {
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ProductDetails"+product.getStk_code(), response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray DetailsArray = jsonResponse.optJSONArray("getStockDetailsResult");
                        JSONArray offersArray = jsonResponse.optJSONArray("getStockOffers");
                        LoadOffers(offersArray);
                        LoadProductDetails(DetailsArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            }) {
            };
            try {
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String ProductDetails = preferences.getString("ProductDetails"+product.getStk_code(), "");
            if (!ProductDetails.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(ProductDetails);
                    JSONArray DetailsArray = jsonResponse.optJSONArray("getStockDetailsResult");
                    LoadProductDetails(DetailsArray);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void LoadOffers(JSONArray offersArray) throws JSONException {
        try {
        productOffers = new ArrayList<>();
        for (int j = 0; j < offersArray.length(); j++) {
            JSONObject offersOBJ = offersArray.getJSONObject(j);
               productOffers.add(new Offer(offersOBJ.optString("offer_no"),offersOBJ.optString("offer_description"),offersOBJ.optString("offer_title"),offersOBJ.optString("offer_image"))) ;
        }
        if (productOffers.size() > 0 )
        {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.offers_header)), 1);

        }
        else if (!Objects.equals(product.getCategoryModel().getCat_code(), "09"))
        {
                tabLayout.removeAllTabs();
        }
        productDetailsAdapter.setProduct(product);
        productDetailsAdapter.setProductoffer(productOffers);
        viewPager2.setAdapter(productDetailsAdapter);
        productDetailsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void LoadProductDetails(JSONArray DetailsArray) {
        try {
            productSpecs = new ArrayList<>();
            productColors = new ArrayList<>();
            for (int j = 0; j < DetailsArray.length(); j++) {
                JSONObject DetailOBJ = DetailsArray.getJSONObject(j);
                try {
                    if (DetailOBJ.optString("value").startsWith("#") || DetailOBJ.optString("value_Ar").startsWith("#")) {

                        productColors.add(new ProductColor(DetailOBJ.optString("spec_title"), DetailOBJ.optString("value"), DetailOBJ.optString("image_link")));
                    } else {
                        productSpecs.add(new ProductSpecs(DetailOBJ.optString("spec_title"), DetailOBJ.optString("value"), DetailOBJ.optString("image_link")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (product.getCategoryModel().getCat_code().equals("00")) {
                productColorAdapter = new ProductColorAdapter(context, productColors);
                ColorsRecycleview.setAdapter(productColorAdapter);//.notifyDataSetChanged();
                productColorAdapter.notifyDataSetChanged();
                productColorAdapter.setOnClickListener(new ProductColorAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductColor productColor) {
                        Picasso.get().load(productColor.getImage_Link()).fit().centerInside().into(product_image);
                        productColorAdapter.setSelectedItem(position);
                        productColorAdapter.notifyDataSetChanged();
                        ProductSliderSwipe(position);
                    }
                });

                int count = 0;
                for (ProductColor item : productColors) {
                    productSlide.add(count, new SlideModel(item.getImage_Link(), "", ScaleTypes.CENTER_INSIDE));
                    count++;
                }
                product_images.setImageList(productSlide);
                product_images.setItemChangeListener(new ItemChangeListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onItemChanged(int i) {
                        productColorAdapter.setSelectedItem(i);
                        productColorAdapter.notifyDataSetChanged();
                        Log.d(TAG, "pos :" + i);
                    }
                });
                product_image.setVisibility(View.GONE);
            } else {
                prod_det.setVisibility(View.INVISIBLE);
                product_main_name.setVisibility(View.VISIBLE);
                product_main_name.setText(product.getProduct_title());
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0
                );
                pro_img.setLayoutParams(param);
                productColorAdapter = new ProductColorAdapter(context, productColors);
                ColorsRecycleview.setAdapter(productColorAdapter);//.notifyDataSetChanged();
                productColorAdapter.notifyDataSetChanged();
                int count = 0;
                for (ProductColor item : productColors) {
                    productSlide.add(count, new SlideModel(item.getImage_Link(), "", ScaleTypes.CENTER_INSIDE));
                    count++;
                }
                product_images.setImageList(productSlide);
                product_image.setVisibility(View.GONE);
            }


            productDetailsAdapter.setProduct(product);
            productDetailsAdapter.setProductSpecs(productSpecs);
            viewPager2.setAdapter(productDetailsAdapter);
            productDetailsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //   because image slider class dose not have swipe method
    public void ProductSliderSwipe(int position) {
        try {
            int count = 1;
            productSlide = new ArrayList<>();
            product_images = Detailesbinding.productImages;
            product_images = new ImageSlider(context);
            ProductColor thisprocolor = productColors.get(position);
            productSlide.add(0, new SlideModel(thisprocolor.getImage_Link(), "", ScaleTypes.CENTER_INSIDE));
            for (ProductColor item : productColors) {
                if (!item.getColor_code().equals(thisprocolor.getColor_code())) {
                    productSlide.add(count, new SlideModel(item.getImage_Link(), "", ScaleTypes.CENTER_INSIDE));
                    count++;
                }
            }
            Detailesbinding.productImages.setImageList(productSlide);
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        show();
        Detailesbinding = null;
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