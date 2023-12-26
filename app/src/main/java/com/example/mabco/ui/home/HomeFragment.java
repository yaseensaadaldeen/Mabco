package com.example.mabco.ui.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mabco.Adapters.CategoriesAdapter;
import com.example.mabco.Adapters.OffersAdapter;
import com.example.mabco.Adapters.ProductsHomeAdapter;
import com.example.mabco.Adapters.SliderAdapter;
import com.example.mabco.Classes.CategoryModel;
import com.example.mabco.Classes.Offer;
import com.example.mabco.Classes.Product;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;
import com.example.mabco.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {
    public RecyclerView categoriesRecycler, offersRecycler, offerproductsRecycler, newproductsRecycler;
    public CategoryModel[] categoryModels;
    public Offer[] offers;
    public ArrayList<SlideModel> Slides;
    public ArrayList<Product> offerproducts, newproducts;
    public CategoriesAdapter categoriesAdapter;
    public OffersAdapter offersAdapter;
    public ProductsHomeAdapter offerproductsHomeAdapter, newproductsHomeAdapter;
    public SliderView imageSlider;
    public RequestQueue requestQueue;
    public Context context;
    NavController navController;
    private ActivityMainBinding binding;
    SharedPreferences preferences;
    View view;
    private SliderAdapter adapter;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        try {
            assert context != null;
            preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
                     Resources standardResources = context.getResources();
            AssetManager assets = standardResources.getAssets();
            DisplayMetrics metrics = standardResources.getDisplayMetrics();
            Configuration config = new Configuration(standardResources.getConfiguration());
            config.setLocale(new Locale("ar"));//getDefault());
            config.setLayoutDirection(new Locale("ar"));
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
            if (navBar != null && navBar.getVisibility() == View.INVISIBLE)
                showNavigationBar();
            imageSlider = view.findViewById(R.id.image_slider);
            requestQueue = Volley.newRequestQueue(this.getContext());


             LoadSliders(getContext(), haveNetworkConnection()) ;

            categoriesRecycler = view.findViewById(R.id.categories);
            categoriesRecycler.setAdapter(LoadCategories(context));

            offersRecycler = view.findViewById(R.id.offers_slider);
            offersRecycler.setAdapter(LoadOffers(context));

            offerproductsRecycler = view.findViewById(R.id.offer_products_slider);
            if (offerproducts == null) {
                ProductsWithDiscountAPI(context, haveNetworkConnection());
            } else {
                offerproductsHomeAdapter = new ProductsHomeAdapter(context, offerproducts);
                offerproductsRecycler.setAdapter(offerproductsHomeAdapter);//.notifyDataSetChanged();
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
            newproductsRecycler = view.findViewById(R.id.new_products_slider);
            if (newproducts == null) {
                NewProductsAPI(context, haveNetworkConnection());
            } else {
                newproductsHomeAdapter = new ProductsHomeAdapter(context, newproducts);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public CategoriesAdapter LoadCategories(Context context) {
        categoryModels = new CategoryModel[]{
                new CategoryModel(getString(R.string.Mobiles), "00", R.drawable.phone_category),
                new CategoryModel(getString(R.string.power), "09", R.drawable.battaries_category),
                new CategoryModel(getString(R.string.Mobile_acc), "01", R.drawable.accessories_category),
                new CategoryModel(getString(R.string.spare), "02", R.drawable.maintanence_category),
                new CategoryModel(getString(R.string.Gaming), "07", R.drawable.games_categorty)
        };
        categoriesRecycler = new RecyclerView(context);

        categoriesAdapter = new CategoriesAdapter(context, categoryModels);
        categoriesRecycler.setAdapter(categoriesAdapter);
        return categoriesAdapter;
    }

    public OffersAdapter LoadOffers(Context context) {
        offers = new Offer[]{
                new Offer("", R.drawable.soon),
                new Offer("", R.drawable.discount),
                new Offer("", R.drawable.discountpersent)
        };
        offersRecycler = new RecyclerView(context);
        offersAdapter = new OffersAdapter(context, offers);
        offersRecycler.setAdapter(offersAdapter);
        return offersAdapter;
    }

    public void ProductsWithDiscountAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.example.mabco.HttpsTrustManager.allowAllSSL();
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
                            offerproductsHomeAdapter.notifyDataSetChanged();
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
            String ProductsWithDiscount = preferences.getString("ProductsWithDiscount", "");
            if (!ProductsWithDiscount.equals("")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(ProductsWithDiscount);
                    JSONArray array = jsonResponse.optJSONArray("GetAllProductsWithDiscount");
                    if (array != null) {
                        LoadProducts(array);
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
            com.example.mabco.HttpsTrustManager.allowAllSSL();
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
                            newproductsHomeAdapter.notifyDataSetChanged();
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
            try {
                String NewArrivals = preferences.getString("NewArrivals", "");
                if (!NewArrivals.equals("")) {
                    JSONObject jsonResponse = new JSONObject(NewArrivals);
                    JSONArray array = jsonResponse.optJSONArray("getNewArrivalsResult");
                    if (array != null) {
                        LoadNewProducts(array);
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
                    Product product = new Product(arrayObj.optString("stk_code"),
                            arrayObj.optString("device_title"),
                            arrayObj.optString("stk_desc"),
                            arrayObj.optString("shelf_price"),
                            new CategoryModel(arrayObj.optString("cat_code")),
                            "0",
                            "0",
                            arrayObj.optString("tag"),
                            "https://" + arrayObj.optString("image_link"));

                    newproducts.add(product);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        newproductsHomeAdapter = new ProductsHomeAdapter(context, newproducts);
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
                    Product product = new Product(arrayObj.optString("stk_code"),
                            arrayObj.optString("device_title"),
                            arrayObj.optString("stk_desc"),
                            arrayObj.optString("shelf_price"),
                            new CategoryModel(arrayObj.optString("cat_code")),
                            arrayObj.optString("discount"),
                            arrayObj.optString("coupon"),
                            "offer",
                            "https://mabcoonline.com/" + arrayObj.optString("image_link"));

                    offerproducts.add(product);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        offerproductsHomeAdapter = new ProductsHomeAdapter(context, offerproducts);
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

    public ArrayList<SlideModel> LoadSliders(Context context, boolean online) {
        requestQueue = Volley.newRequestQueue(context);
        com.example.mabco.HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/getsliderimages";
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

                            adapter = new SliderAdapter(context,array);
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
                            LoadSliders(context , false);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            JSONArray array = null;
                            try {
                                LoadSliders(context , false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        }
        else
        {
            String sliderimages = preferences.getString("sliderimages", "");
            if (!sliderimages.equals("")) {

                try {
                    JSONObject jsonResponse = new JSONObject(sliderimages);
                    JSONArray array = jsonResponse.optJSONArray("getSliderImagesResult");
                    if (array != null) {

                        adapter = new SliderAdapter(context,array);
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
        return Slides;
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
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}