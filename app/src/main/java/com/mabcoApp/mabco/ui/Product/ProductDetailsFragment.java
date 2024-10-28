package com.mabcoApp.mabco.ui.Product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.mabcoApp.mabco.Adapters.ProductColorAdapter;
import com.mabcoApp.mabco.Adapters.ProductDetailsAdapter;
import com.mabcoApp.mabco.Adapters.SliderAdapter;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.ProductColor;
import com.mabcoApp.mabco.Classes.ProductSpecs;
import com.mabcoApp.mabco.Classes.ShoppingCart;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.databinding.FragmentProductDetailesBinding;
import com.mabcoApp.mabco.databinding.FragmentProductSpecsBinding;
import com.smarteist.autoimageslider.SliderPager;
import com.smarteist.autoimageslider.SliderView;
import com.varunest.sparkbutton.SparkButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProductDetailsFragment extends Fragment {
    private FragmentProductDetailesBinding Detailesbinding;
    private RequestQueue requestQueue;
    public Context context;
    NavController navController;
    public RecyclerView ColorsRecycleview;
    public ArrayList<ProductSpecs> productSpecs;
    public ArrayList<ProductColor> productColors;
    public ArrayList<Offer> productOffers;
    public ProductColorAdapter productColorAdapter;
    public ImageView product_image;
    public SliderView product_images;
    public TextView product_name, product_price, product_disc, product_main_name, txt_items_count;
    public Product product;
    public ArrayList<SlideModel> productSlide = new ArrayList<>();
    public ArrayList<String> ImagesURL;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ProductDetailsAdapter productDetailsAdapter;
    private FloatingActionButton backbtn;
    private RelativeLayout prod_det;
    private LinearLayout add_to_shopping_btn;
    private MaterialCardView pro_img;
    SharedPreferences preferences, ShoppingCartData, PersonalPreference,Token;
    String local;
    int shoppingcartItems;
    SparkButton shopping_cart_btn;
    View view;
    private SliderAdapter adapter;
    ShimmerFrameLayout ProductImageViewContainer;
    String Destenation = "";
    FragmentProductSpecsBinding Specsbinding;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Detailesbinding = FragmentProductDetailesBinding.inflate(inflater, container, false);
            Specsbinding = FragmentProductSpecsBinding.inflate(inflater, container, false);
            view = inflater.inflate(R.layout.fragment_product_detailes, container, false);
            context = getContext();
            assert context != null;
            preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
            ShoppingCartData = context.getSharedPreferences("ShoppingCartData", Context.MODE_PRIVATE);
            PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
            Token =context.getSharedPreferences("Token", Context.MODE_PRIVATE);

            hide();
            shoppingcartItems = ShoppingCartData.getInt("items_count", 0);
            if (getArguments() != null) {
                String productJson=getArguments().getString("Product");
                Gson gson = new Gson();
                  product = gson.fromJson(productJson, Product.class);
                // Use the product object to display product details
                if (product == null){
                    ProductDetailsFragmentArgs args = ProductDetailsFragmentArgs.fromBundle(getArguments());
                    product = args.getProduct();
                }
            }
            else {
                ProductDetailsFragmentArgs args = ProductDetailsFragmentArgs.fromBundle(getArguments());
                product = args.getProduct();
            }
            txt_items_count = Detailesbinding.txtItemsCount;
            local = PersonalPreference.getString("Language", "ar");
            if (shoppingcartItems == 0) {
                txt_items_count.setVisibility(View.GONE);
            } else {
                txt_items_count.setVisibility(View.VISIBLE);
                txt_items_count.setText(String.valueOf(shoppingcartItems));
            }
            shopping_cart_btn = Detailesbinding.shoppingCartBtn;

          //  product = args.getProduct();
            prod_det = Detailesbinding.prodDet;
            backbtn = Detailesbinding.backBtn;
            backbtn.setOnClickListener(v -> requireActivity().onBackPressed());
            add_to_shopping_btn = Detailesbinding.addToShoppingBtn;
            //navController = Navigation.findNavController(view);
            final NavHostFragment navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            navController = navHostFragment.getNavController();

            product_main_name = Detailesbinding.productMainName;
            pro_img = Detailesbinding.proImg;
            product_images = Detailesbinding.productImages;
            ColorsRecycleview = Detailesbinding.productColors;
            product_name = Detailesbinding.productName;
           // shopping_cart_btn = Detailesbinding.shoppingCartBtn;
            shopping_cart_btn.setOnClickListener(v -> {
                navController.navigate(R.id.action_productDetailsFragment_to_shoppingCartFragment);
            });
            productDetailsAdapter = new ProductDetailsAdapter(this);
            tabLayout = Detailesbinding.tabLayout;
            viewPager2 = Detailesbinding.pager;
            viewPager2.setAdapter(productDetailsAdapter);
            add_to_shopping_btn.setEnabled(false);

            add_to_shopping_btn.setOnClickListener(v -> addItemToCart());
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.details)), 0);
            if (product.getCategoryModel().getCat_code().equals("09"))
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.FAQ)), 1);
            Resources standardResources = context.getResources();
            Configuration config = new Configuration(standardResources.getConfiguration());
            if (config.getLayoutDirection() == Layout.DIR_LEFT_TO_RIGHT) {
                backbtn.setImageResource(R.drawable.back_rtl);
                add_to_shopping_btn.setBackground(this.getResources().getDrawable(R.drawable.shopping_cart_btn_ltr));
            }
            product_price = Detailesbinding.productPrice;
            product_disc = Detailesbinding.productDisc;
            product_image = Detailesbinding.productImage;
            product_name.setText(product.getProduct_title());
            product_price.setText(formatPrice(product.getShelf_price(),local));
            ProductImageViewContainer  =Detailesbinding.ProductImageViewContainer;
            ProductImageViewContainer.startShimmer();
            if (!product.getDiscount().equals("0")) {
                product_disc.setVisibility(View.VISIBLE);
                if (product.getCoupon().equals("0")) {
                    product_disc.setText(formatPrice(product.getShelf_price(), local));
                    String final_price = String.valueOf((Long.parseLong(product.getShelf_price()) - (Long.parseLong(product.getDiscount()))));
                    product_price.setText(formatPrice(final_price, local));
                    product_disc.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    product_disc.setTextColor(Color.RED);
                } else {
                    product_disc.setPaintFlags(product_price.getPaintFlags());
                    product_disc.setTextColor(Color.parseColor("#af0491cf"));
                    product_disc.setText(local.equals("ar")?  "  قسيمة شرائية بقيمة " +  formatPrice(product.getDiscount(),local):"With Coupon " +  formatPrice(product.getDiscount(),local));
                }
            }
            ShimmerFrameLayout shimmerFrameLayout = Specsbinding.shimmiringSpec;
            ShimmerFrameLayout shimmiringSpecs= Specsbinding.shimmiringSpecs;
            shimmerFrameLayout.startShimmer();
            shimmiringSpecs.startShimmer();
            boolean online = haveNetworkConnection();
            if (savedInstanceState==null)
            ProductDetailsAPI(context, online);
            else
                ProductDetailsAPI(context, false);
            productDetailsAdapter.setProduct(product);


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
            Log.d("errorporductdetailes", Objects.requireNonNull(e.getMessage()));

            Toast.makeText(getContext(), "Error: Product not found!", Toast.LENGTH_SHORT).show();

            // navController.navigate(R.id.action_productDetailsFragment_to_nav_home);
            // Navigate back
            // requireActivity().onBackPressed();
            e.printStackTrace();
        }
        return Detailesbinding.getRoot();
    }

    public static String formatPrice(String priceString, String Lang) {
        try {
            // Parse the string to a long
            long priceValue = Long.parseLong(priceString.replace(",","").replace(".00",""));

            // Get a NumberFormat instance for formatting with US locale (uses commas)
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

            // Format the long value
            return numberFormat.format(priceValue) + (Lang.equals("ar") ? " ل.س " : " SP");
        } catch (NumberFormatException e) {
            // Handle the exception if parsing fails
            e.printStackTrace();
            return "Invalid Price";
        }
    }

    public void ProductDetailsAPI(Context context, boolean online) {
        requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        String UserID = Token.getString("UserID" , "0");
        String url = UrlEndPoint.General + "Service1.svc/getStockDetails/" + product.getStk_code() + ","+local+",0,"+UserID;
        if (online) {
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ProductDetails"+product.getStk_code(), response);
                        editor.putString("ProductOffers"+product.getStk_code(), response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray DetailsArray = jsonResponse.optJSONArray("getStockDetailsResult");
                        JSONArray offersArray = jsonResponse.optJSONArray("getStockOffers");
                        LoadOffers(offersArray);
                        LoadProductDetails(DetailsArray);
                        add_to_shopping_btn.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    ProductDetailsAPI(context,false);
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
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    // Cache the response
                    if (response.headers.get("Cache-Control") == null) {
                        response.headers.put("Cache-Control", "max-age=1800"); // Cache for 1 hour
                    }
                    return super.parseNetworkResponse(response);
                }

            };
            try {
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String ProductDetails = preferences.getString("ProductDetails"+product.getStk_code(), "");
            String ProductOffers = preferences.getString("ProductDetails"+product.getStk_code(), "");
            if (!ProductDetails.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(ProductDetails);
                    JSONArray DetailsArray = jsonResponse.optJSONArray("getStockDetailsResult");
                    LoadProductDetails(DetailsArray);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (!ProductOffers.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(ProductOffers);
                    JSONArray offersArray = jsonResponse.optJSONArray("getStockOffers");
                    LoadOffers(offersArray);
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
               productOffers.add(new Offer(offersOBJ.optString("offer_no"),offersOBJ.optString("offer_description"),offersOBJ.optString("offer_title"),"mabcoonline.com/"+offersOBJ.optString("offer_image"))) ;
        }
        if (productOffers.size() > 0 )
        {
            if (product.getCategoryModel().getCat_code().equals("09"))
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.offers_header)), 2);
            else
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.offers_header)), 1);

        }
        else if (!Objects.equals(product.getCategoryModel().getCat_code(), "09"))
        {
            tabLayout.removeAllTabs();
            tabLayout.setVisibility(View.GONE);
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
                        productColors.add(new ProductColor(DetailOBJ.optString("stk_code"), DetailOBJ.optString("spec_title"), DetailOBJ.optString("value"), DetailOBJ.optString("image_link")));
                    } else {
                        productSpecs.add(new ProductSpecs(DetailOBJ.optString("spec_title"), DetailOBJ.optString("value"), DetailOBJ.optString("image_link")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ProductImageViewContainer.setVisibility(View.GONE);
            ProductImageViewContainer.stopShimmer();
            if (product.getCategoryModel().getCat_code().equals("00")) {
                productColorAdapter = new ProductColorAdapter(context, productColors);
                product.setProductColor(productColors.get(0));
                product.setStk_code(productColors.get(0).getStk_code());
                ColorsRecycleview.setAdapter(productColorAdapter);//.notifyDataSetChanged();
                productColorAdapter.notifyDataSetChanged();
                productColorAdapter.setOnClickListener(new ProductColorAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, ProductColor productColor) {
                        //Picasso.get().load(productColor.getImage_Link()).fit().centerInside().into(product_image);
                        Glide.with(context)
                                .load(productColor.getImage_Link()) // URL or resource ID
                                .fitCenter()
                                .into(product_image);
                        productColorAdapter.setSelectedItem(position);
                        productColorAdapter.notifyDataSetChanged();
                        product.setStk_code(productColor.getStk_code());
                        product.setProduct_image(productColor.getImage_Link());
                        product.setProductColor(productColor);
                        product_images.setCurrentPagePosition(position);
                        //ProductSliderSwipe(position);
                    }
                });
                productSlide = new ArrayList<>();
                ImagesURL=new ArrayList<>();

                int count = 0;
                for (ProductColor item : productColors) {
                    productSlide.add(count, new SlideModel(item.getImage_Link(), "", ScaleTypes.CENTER_INSIDE));
                    ImagesURL.add(item.getImage_Link());
                    count++;
                }
                adapter = new SliderAdapter(context,ImagesURL);
                product_images.setSliderAdapter(adapter);
                adapter.notifyDataSetChanged();

                //product_images.setImageList(productSlide);
                product_images.setDrawingCacheEnabled(true);
                product_images.getSliderPager().addOnPageChangeListener(new SliderPager.OnPageChangeListener(){
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        SlideModel current_item = productSlide.get(position);
                        int selected_color_index = productColorAdapter.getSelectedimage(current_item.getImageUrl());
                        productColorAdapter.setSelectedItem(selected_color_index);
                        ColorsRecycleview.scrollToPosition(selected_color_index);
                        productColorAdapter.notifyDataSetChanged();
                        product.setStk_code(productColorAdapter.selectedColor.getStk_code());
                        product.setProduct_image(productColorAdapter.selectedColor.getImage_Link());
                        product.setProductColor(productColorAdapter.selectedColor);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        // This method will be called when the scroll state changes
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
                product.setProductColor(productColors.get(0));
                ColorsRecycleview.setAdapter(productColorAdapter);//.notifyDataSetChanged();
                productColorAdapter.notifyDataSetChanged();
                productSlide = new ArrayList<>();
                ImagesURL=new ArrayList<>();
                int count = 0;
                for (ProductColor item : productColors) {
                    productSlide.add(count, new SlideModel(item.getImage_Link(), "", ScaleTypes.CENTER_INSIDE));
                    ImagesURL.add(item.getImage_Link());
                    count++;
                }
                adapter = new SliderAdapter(context,ImagesURL);
                product_images.setSliderAdapter(adapter);
                adapter.notifyDataSetChanged();
                product_image.setVisibility(View.GONE);
            }


            // Initialize and start the shimmer effect

            productDetailsAdapter.setProduct(product);
            productDetailsAdapter.setProductSpecs(productSpecs);
            viewPager2.setAdapter(productDetailsAdapter);
            productDetailsAdapter.notifyDataSetChanged();
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
        if (navBar != null) navBar.setVisibility(View.INVISIBLE);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
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
        //if (!Destenation.equals("SignIn")) show();
        Detailesbinding = null;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE.toString());
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void addItemToCart() {
        if (ShoppingCart.addToCart(context, product)) {
            try {
                shoppingcartItems = ShoppingCart.getItemCount(context);
                txt_items_count.setText(String.valueOf(shoppingcartItems));
                txt_items_count.setVisibility(View.VISIBLE);
                // for updating the main top bar shopping cart items count
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).updateNotificationCount(shoppingcartItems);
                Toast.makeText(context, getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();
                shopping_cart_btn.playAnimation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(context, getString(R.string.already_in_cart), Toast.LENGTH_SHORT).show();
    }
}