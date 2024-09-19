package com.mabcoApp.mabco.ui.Home;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mabcoApp.mabco.Adapters.CategoriesAdapter;
import com.mabcoApp.mabco.Adapters.OfferAdapter;
import com.mabcoApp.mabco.Adapters.ProductsHomeAdapter;
import com.mabcoApp.mabco.Adapters.SliderAdapter;
import com.mabcoApp.mabco.Classes.CategoryModel;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.ui.Offers.OffersFullScreenFragment;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    public RecyclerView categoriesRecycler, offersRecycler, offerproductsRecycler, newproductsRecycler;
    public CategoryModel[] categoryModels;
    public ArrayList<Offer> offers;
    public ArrayList<SlideModel> Slides;
    public ArrayList<Product> offerproducts, newproducts;
    public CategoriesAdapter categoriesAdapter;
    public OfferAdapter offerAdapter;
    public ProductsHomeAdapter offerproductsHomeAdapter, newproductsHomeAdapter;
    public SliderView imageSlider;
    public RequestQueue requestQueue;
    public Context context;
    String vir;
    String version;
    String link;
    NavController navController;
    SharedPreferences preferences, PersonalPreference , Token ;
    String local;
    View view;
    ShimmerFrameLayout offershimmerViewContainer, newshimmerViewContainer,offer_item_shimmer_view_container;
    private SliderAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE) showNavigationBar();
        imageSlider = view.findViewById(R.id.image_slider);
        requestQueue = Volley.newRequestQueue(this.getContext());
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1); // Cancel the notification with ID 1
        categoriesRecycler = view.findViewById(R.id.categories);
        categoriesRecycler.setAdapter(LoadCategories(context));
        newshimmerViewContainer = view.findViewById(R.id.new_shimmer_view_container);
        offerproductsRecycler = view.findViewById(R.id.offer_products_slider);
        newproductsRecycler = view.findViewById(R.id.new_products_slider);
        offersRecycler = view.findViewById(R.id.offers_slider);
        offershimmerViewContainer = view.findViewById(R.id.offershimmerViewContainer);
        offer_item_shimmer_view_container = view.findViewById(R.id.offer_item_shimmer_view_container);
        offer_item_shimmer_view_container.startShimmer();
        offershimmerViewContainer.startShimmer();
        newshimmerViewContainer.startShimmer();
        SetLanguage(local);
        categoriesAdapter.setOnClickListener(new CategoriesAdapter.OnClickListener() {
            @Override
            public void onClick(int position, CategoryModel categoryModel) {
                try {
                    NavController navController = Navigation.findNavController(view);

                    Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_products);
                    navController.navigateUp();
                    navController.navigate((NavDirections) HomeFragmentDirections.actionNavHomeToNavProducts(categoryModel.getCat_code(), categoryModel.getTitle()));

                } catch (Exception e) {
                    Log.i("Exception", e.getMessage());
                }
            }

        });
        try {
            if (savedInstanceState != null) {
                assert context != null;
                LoadSliders(getContext(), haveNetworkConnection());
                OffersAPI(context, haveNetworkConnection());

                if (offerproducts == null) {
                    ProductsWithDiscountAPI(context, haveNetworkConnection());
                } else {
                    offerproductsHomeAdapter = new ProductsHomeAdapter(context, offerproducts, ((MainActivity) getActivity()), local);
                    offerproductsRecycler.setAdapter(offerproductsHomeAdapter);//.notifyDataSetChanged();
                    offershimmerViewContainer.stopShimmer();
                    offershimmerViewContainer.setVisibility(View.GONE);
                    offerproductsHomeAdapter.notifyDataSetChanged();
                    offerproductsHomeAdapter.setOnClickListener(new ProductsHomeAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position, Product product) {
                            navController = Navigation.findNavController(view);
                            Navigation.findNavController(view).navigate(R.id.action_nav_home_to_productDetailsFragment);
                            navController.navigateUp();
                            navController.navigate((NavDirections) HomeFragmentDirections.actionNavHomeToProductDetailsFragment(product));
                        }
                    });

                }
                if (newproducts == null) {
                    NewProductsAPI(context, haveNetworkConnection());
                } else {
                    newproductsHomeAdapter = new ProductsHomeAdapter(context, newproducts, ((MainActivity) getActivity()), local);
                    newproductsRecycler.setAdapter(newproductsHomeAdapter);//.notifyDataSetChanged();
                    newproductsHomeAdapter.setOnClickListener(new ProductsHomeAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position, Product product) {
                            navController = Navigation.findNavController(view);
                            Navigation.findNavController(view).navigate(R.id.action_nav_home_to_productDetailsFragment);
                            navController.navigateUp();
                            navController.navigate((NavDirections) HomeFragmentDirections.actionNavHomeToProductDetailsFragment(product));

                        }
                    });
                    newshimmerViewContainer.stopShimmer();
                    newshimmerViewContainer.setVisibility(View.GONE);
                    newproductsHomeAdapter.notifyDataSetChanged();
                }
                PackageInfo pInfo = null;
                try {
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                    version = pInfo.versionName;
                } catch (Exception e) {
                    e.printStackTrace();
                    version = "";
                }

                int verCode = pInfo.versionCode;
            }
            else {
                newproductsRecycler = view.findViewById(R.id.new_products_slider);
                newproductsRecycler.setAdapter(newproductsHomeAdapter);
                newshimmerViewContainer.stopShimmer();
                newshimmerViewContainer.setVisibility(View.GONE);
                newproductsHomeAdapter.notifyDataSetChanged();

                offerproductsRecycler = view.findViewById(R.id.offer_products_slider);
                offerproductsRecycler.setAdapter(offerproductsHomeAdapter);//.notifyDataSetChanged();
                offershimmerViewContainer.stopShimmer();
                offershimmerViewContainer.setVisibility(View.GONE);
                offerproductsHomeAdapter.notifyDataSetChanged();
                LoadSliders(getContext(), false);
                OffersAPI(context,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public CategoriesAdapter LoadCategories(Context context) {
        categoryModels = new CategoryModel[]{new CategoryModel(getString(R.string.Mobiles), "00", R.drawable.phone_category), new CategoryModel(getString(R.string.power), "09", R.drawable.battaries_category), new CategoryModel(getString(R.string.Mobile_acc), "01", R.drawable.accessories_category), new CategoryModel(getString(R.string.spare), "02", R.drawable.maintanence_category), new CategoryModel(getString(R.string.Gaming), "07", R.drawable.games_categorty)};
        categoriesRecycler = new RecyclerView(context);

        categoriesAdapter = new CategoriesAdapter(context, categoryModels);
        categoriesRecycler.setAdapter(categoriesAdapter);
        return categoriesAdapter;
    }

    public void OffersAPI(Context context, boolean online) {
        requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/getoffers/" + local;
        if (online) {
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("OffersDetails", response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray offersArray = jsonResponse.optJSONArray("GetOffersResult");
                        LoadOffers(offersArray);
                        offer_item_shimmer_view_container.stopShimmer();
                        offer_item_shimmer_view_container.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    OffersAPI(context,false);
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
            String Offers = preferences.getString("OffersDetails", "");
            if (!Offers.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(Offers);
                    JSONArray offersArray = jsonResponse.optJSONArray("GetOffersResult");
                    LoadOffers(offersArray);
                    offer_item_shimmer_view_container.stopShimmer();
                    offer_item_shimmer_view_container.setVisibility(View.GONE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadOffers(JSONArray offersArray) throws JSONException {
        try {

            offers = new ArrayList<>();
            JSONObject offersOBJ = offersArray.getJSONObject(0);
            offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), "mabcoonline.com/images/soon.png"));
            offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), "mabcoonline.com/images/discount.png"));
            offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), "mabcoonline.com/images/discountpersent.png"));

            for (int j = 0; j < offersArray.length(); j++) {
                offersOBJ = offersArray.getJSONObject(j);
                offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), offersOBJ.optString("Image_link")));
            }

            offerAdapter = new OfferAdapter(context, offers, "Home");
            offersRecycler.setAdapter(offerAdapter);
            offerAdapter.setOnClickListener(new OfferAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Offer offer) {
                    openFullScreenFragment(position, offer);
                }
            });
            offerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFullScreenFragment(int position, Offer offer) {
        ArrayList<Offer> offerList = new ArrayList<>(offers);
        OffersFullScreenFragment fragment = OffersFullScreenFragment.newInstance(position, offerList);


        // Launch the fragment
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(this.getId(), fragment) // Ensure you have a container for fragments
                .addToBackStack(null) // Add to back stack if you want to navigate back
                .commit();
    }


    public void ProductsWithDiscountAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/GetAllProductsWithDiscount";
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ProductsWithDiscount", response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("GetAllProductsWithDiscount");

                        if (array != null) {
                            LoadProducts(array);
                            offershimmerViewContainer.stopShimmer();
                            offershimmerViewContainer.setVisibility(View.GONE);
                            offerproductsHomeAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    ProductsWithDiscountAPI(context,false);
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
            String ProductsWithDiscount = preferences.getString("ProductsWithDiscount", "");
            if (!ProductsWithDiscount.equals("")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(ProductsWithDiscount);
                    JSONArray array = jsonResponse.optJSONArray("GetAllProductsWithDiscount");
                    if (array != null) {
                        LoadProducts(array);
                        offershimmerViewContainer.stopShimmer();
                        offershimmerViewContainer.setVisibility(View.GONE);
                        offerproductsHomeAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void NewProductsAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/getNewArrivals";
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("NewArrivals", response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("getNewArrivalsResult");
                        if (array != null) {
                            LoadNewProducts(array);
                            newshimmerViewContainer.stopShimmer();
                            newshimmerViewContainer.setVisibility(View.GONE);
                            newproductsHomeAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    NewProductsAPI(context,false);
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
            try {
                String NewArrivals = preferences.getString("NewArrivals", "");
                if (!NewArrivals.equals("")) {
                    JSONObject jsonResponse = new JSONObject(NewArrivals);
                    JSONArray array = jsonResponse.optJSONArray("getNewArrivalsResult");
                    if (array != null) {
                        LoadNewProducts(array);
                        newshimmerViewContainer.stopShimmer();
                        newshimmerViewContainer.setVisibility(View.GONE);
                        newproductsHomeAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void LoadNewProducts(JSONArray array) throws JSONException {
        try {

            newproducts = new ArrayList<Product>();
            try {
                for (int i = 0; i < array.length(); i++) {

                    JSONObject arrayObj = array.getJSONObject(i);
                    Product product = new Product(arrayObj.optString("stk_code"), arrayObj.optString("device_title"), arrayObj.optString("stk_desc"), arrayObj.optString("shelf_price"), new CategoryModel(arrayObj.optString("cat_code")), arrayObj.optString("discount"), arrayObj.optString("coupon"), arrayObj.optString("tag"), "https://"+ arrayObj.optString("image_link"));

                    newproducts.add(product);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        newproductsHomeAdapter = new ProductsHomeAdapter(context, newproducts, ((MainActivity) getActivity()),local);
        newproductsRecycler.setAdapter(newproductsHomeAdapter);//.notifyDataSetChanged();
        newproductsHomeAdapter.setOnClickListener(new ProductsHomeAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Product product) {
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_productDetailsFragment);
                navController.navigateUp();
                navController.navigate((NavDirections) HomeFragmentDirections.actionNavHomeToProductDetailsFragment(product));

            }
        });
        newproductsHomeAdapter.notifyDataSetChanged();
    }

    private void LoadProducts(JSONArray array) throws JSONException {
        try {

            offerproducts = new ArrayList<Product>();
            try {
                for (int i = 0; i < array.length(); i++) {

                    JSONObject arrayObj = array.getJSONObject(i);
                    Product product = new Product(arrayObj.optString("stk_code"), arrayObj.optString("device_title"), arrayObj.optString("stk_desc"), arrayObj.optString("shelf_price"), new CategoryModel(arrayObj.optString("cat_code")), arrayObj.optString("discount"), arrayObj.optString("coupon"), "offer", "https://mabcoonline.com/" + arrayObj.optString("image_link"));

                    offerproducts.add(product);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        offerproductsHomeAdapter = new ProductsHomeAdapter(context, offerproducts, ((MainActivity) getActivity()),local);
        offershimmerViewContainer.stopShimmer();
        offershimmerViewContainer.setVisibility(View.GONE);
        offerproductsRecycler.setAdapter(offerproductsHomeAdapter);//.notifyDataSetChanged();
        offerproductsHomeAdapter.setOnClickListener(new ProductsHomeAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Product product) {
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_productDetailsFragment);
                navController.navigateUp();
                navController.navigate((NavDirections) HomeFragmentDirections.actionNavHomeToProductDetailsFragment(product));
            }
        });
        offerproductsHomeAdapter.notifyDataSetChanged();
    }

    public void LoadSliders(Context context, boolean online) {
        requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        String UserID = Token.getString("UserID" , "0");
        //inserting home page log with the slider returned data
        String url = UrlEndPoint.General + "Service1.svc/getsliderimages/"+UserID;
        Slides = new ArrayList<>();
        if (online) {

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("sliderimages", response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("getSliderImagesResult");
                        if (array != null) {

                            adapter = new SliderAdapter(context, array);
                            imageSlider.setSliderAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            imageSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                            imageSlider.setIndicatorSelectedColor(Color.WHITE);
                            imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            imageSlider.setScrollTimeInSec(3);
                            imageSlider.setAutoCycle(true);
                            imageSlider.startAutoCycle();
                        } else {
                            LoadSliders(context, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    JSONArray array = null;
                    try {
                        LoadSliders(context, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            String sliderimages = preferences.getString("sliderimages", "");
            if (!sliderimages.equals("")) {

                try {
                    JSONObject jsonResponse = new JSONObject(sliderimages);
                    JSONArray array = jsonResponse.optJSONArray("getSliderImagesResult");
                    if (array != null) {

                        adapter = new SliderAdapter(context, array);
                        imageSlider.setSliderAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                        imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                        imageSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                        imageSlider.setIndicatorSelectedColor(Color.WHITE);
                        imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                        imageSlider.setScrollTimeInSec(3);
                        imageSlider.setAutoCycle(true);
                        imageSlider.startAutoCycle();

                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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

    public void SetLanguage(String local) {
        Resources standardResources = context.getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());
        config.setLocale(new Locale(local));//getDefault());
        config.setLayoutDirection(new Locale(local));
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}