package com.mabcoApp.mabco.ui.Product;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mabcoApp.mabco.Adapters.BrandAdapter;
import com.mabcoApp.mabco.Adapters.ProductsHomeAdapter;
import com.mabcoApp.mabco.Classes.Brand;
import com.mabcoApp.mabco.Classes.CategoryModel;
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryProductsFragment extends Fragment {
    private String cat_code = "00";
    NavController navController;
    private RequestQueue requestQueue;
    public Context context;
    public RecyclerView BrandRecycleview;
    public RecyclerView ProductsRecycleview;

    public ArrayList<Brand> brands;
    public ArrayList<Product> brandProducts;
    public BrandAdapter brandAdapter;
    public ProductsHomeAdapter productsAdapter;
    public String cat_name = "";
    public SharedPreferences SharedCategoryProductsData, PersonalPreference, Token;
    String local;
    private View view;
    private String searchtext = "";
    private TextView emptyTextView;
    ShimmerFrameLayout brandshimmerViewContainer, productshimmerViewContainer;
    String from = "";
    int from_position = -1;

    public CategoryProductsFragment() {
    }

    public CategoryProductsFragment(String cat_code, String searchtext, int position) {
        this.searchtext = searchtext;
        this.cat_code = cat_code;
        this.from = "products";
        this.from_position = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category_products, container, false);
        try {


            context = getContext();
            SharedCategoryProductsData = context.getSharedPreferences("CategoryProductsData", Context.MODE_PRIVATE);
            BrandRecycleview = view.findViewById(R.id.brands_slider);
            ProductsRecycleview = view.findViewById(R.id.brand_products_slider);
            emptyTextView = view.findViewById(R.id.emptyTextView);
            brandshimmerViewContainer = view.findViewById(R.id.brand_shimmer_view_container);
            PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
            Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
            local = PersonalPreference.getString("Language", "ar");
            brandshimmerViewContainer.startShimmer();
            productshimmerViewContainer = view.findViewById(R.id.product_shimmer_view_container);
            productshimmerViewContainer.startShimmer();
            if (!from.equals("products")) {
                show();
            }
            int orientation = getResources().getConfiguration().orientation;
            int spanCount;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode
                spanCount = 2;

            } else {
                // code for landscape mode
                spanCount = 4;
            }

            gridLayoutManager.setSpanCount(spanCount);
            ProductsRecycleview.setLayoutManager(gridLayoutManager);
            Bundle bundle = getArguments();

            if (bundle != null) {
                CategoryProductsFragmentArgs args = CategoryProductsFragmentArgs.fromBundle(bundle);
                cat_code = args.getCatCode();
                cat_name = args.getCatName();
            } else {
                switch (cat_code) {
                    case "00":
                        cat_name = getContext().getString(R.string.Mobiles);
                    case "01":
                        cat_name = getContext().getString(R.string.Mobile_acc);
                    case "02":
                        cat_name = getContext().getString(R.string.spare);
                    case "09":
                        cat_name = getContext().getString(R.string.power);
                    case "07":
                        cat_name = getContext().getString(R.string.power);
                }
            }

            BrandProductsAPI(context, NetworkStatus.isOnline(context));


            //  ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(cat_name);

        } catch (Exception e) {
            Log.i("Products Exception", e.getMessage());
        }
        return view;
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

    public void BrandProductsAPI(Context context, boolean isOnline) {
        if (isOnline) {
            SharedPreferences viewpager2 = context.getSharedPreferences("viewpager2", Context.MODE_PRIVATE);
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            String UserID = Token.getString("UserID", "0");

            // This condition exists because ViewPager2 caches the next page by default before opening it, and I don't want to log that event.
// I have stored the selected position in preferences upon selection and then compared if it's from a cached request or a click on a page item.
            if (viewpager2.getInt("position", 0) != from_position && from_position != -1)
                UserID = "0";
            viewpager2.edit().remove("position").apply();
            String url = UrlEndPoint.General + "Service1.svc/getStocksByCat/" + cat_code + "," + UserID;
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray BrandsArray = jsonResponse.optJSONArray("getBrands");
                        JSONArray ProductsArray = jsonResponse.optJSONArray("getStocks");
                        SharedPreferences.Editor editor = SharedCategoryProductsData.edit();
                        editor.putString("CategoryBrandsProducts" + cat_code, response);
                        editor.apply();


                        if (ProductsArray != null) {
                            LoadBrands(BrandsArray, ProductsArray);
                            productshimmerViewContainer.stopShimmer();
                            productshimmerViewContainer.setVisibility(View.GONE);

                            brandshimmerViewContainer.stopShimmer();
                            brandshimmerViewContainer.setVisibility(View.GONE);

                            brandAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    BrandProductsAPI(context, false);
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
                String CategoryBrandsProducts = SharedCategoryProductsData.getString("CategoryBrandsProducts" + cat_code, "");
                JSONObject jsonResponse = new JSONObject(CategoryBrandsProducts);
                JSONArray BrandsArray = jsonResponse.optJSONArray("getBrands");
                JSONArray ProductsArray = jsonResponse.optJSONArray("getStocks");
                LoadBrands(BrandsArray, ProductsArray);
                brandAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void LoadBrandProducts(String brand_code) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            CategoryProductsFragmentArgs args = CategoryProductsFragmentArgs.fromBundle(getArguments());
            cat_name = args.getCatName();
        }
        String brand_name = "";
        ArrayList<Product> products = new ArrayList<>();
        for (int j = 0; j < brands.size(); j++) {
            if (brands.get(j).getBrand_code().equals(brand_code)) {
                products = brands.get(j).getProducts();
                brand_name = " - " + brands.get(j).getBrand_name();
            }
        }
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(cat_name + brand_name);
        productsAdapter = new ProductsHomeAdapter(context, products, ((MainActivity) getActivity()), local);
        if (productsAdapter.getItemCount() == 0) {
            // If the adapter has no items, show the TextView
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            // If the adapter has items, hide the TextView
            emptyTextView.setVisibility(View.GONE);
        }
        ProductsRecycleview.setAdapter(productsAdapter);
        productsAdapter.setOnClickListener(new ProductsHomeAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Product product) {
                try {
                    NavController navController = Navigation.findNavController(view);
                    NavDirections action = CategoryProductsFragmentDirections.actionNavProductsToProductDetailsFragment(product);
                    Navigation.findNavController(view).navigate(action);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });//.notifyDataSetChanged();
        productsAdapter.notifyDataSetChanged();
    }

    public void LoadBrands(JSONArray BrandsArray, JSONArray ProductsArray) {
        try {
            brands = new ArrayList<>();
            for (int j = 0; j < BrandsArray.length(); j++) {
                brandProducts = new ArrayList<>();
                JSONObject BrandOBJ = BrandsArray.getJSONObject(j);
                try {
                    for (int i = 0; i < ProductsArray.length(); i++) {
                        JSONObject ProductOBJ = ProductsArray.getJSONObject(i);
                        Product product = new Product();
                        if ((!cat_code.equals("01") && BrandOBJ.optString("brand_code").equals(ProductOBJ.optString("brand_code"))) || (cat_code.equals("01") && BrandOBJ.optString("acc_cat_id").equals(ProductOBJ.optString("brand_code")))) {
                            product = new Product(ProductOBJ.optString("stk_code"), ProductOBJ.optString("device_title"), ProductOBJ.optString("stk_desc"), ProductOBJ.optString("shelf_price"), new CategoryModel(ProductOBJ.optString("cat_code")), ProductOBJ.optString("discount"), ProductOBJ.optString("coupon"), "", "https://" + ProductOBJ.optString("image_link"));
                            // product = new Product(ProductOBJ.optString("stk_code"), ProductOBJ.optString("device_title"), ProductOBJ.optString("stk_desc"), ProductOBJ.optString("shelf_price"), new CategoryModel(cat_code), "0", "0", "", "https://" + ProductOBJ.optString("image_link"));
                        } else continue;
                        if (!searchtext.isEmpty()) {
                            if (product.matchesSearchText(searchtext)) brandProducts.add(product);
                        } else brandProducts.add(product);

                    }
                    if (brandProducts.size() > 0) {
                        String brand_Code;
                        String brand_name;
                        if (cat_code.equals("01")) {
                            brand_Code = BrandOBJ.optString("acc_cat_id");
                            brand_name = BrandOBJ.optString("acc_cat_name");
                        } else {
                            brand_Code = BrandOBJ.optString("brand_code");
                            brand_name = BrandOBJ.optString("brand_title");
                        }
                        brands.add(new Brand(brand_name, brand_Code, "https://" + BrandOBJ.optString("image_link"), brandProducts));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            brandAdapter = new BrandAdapter(context, brands);
            BrandRecycleview.setAdapter(brandAdapter);//.notifyDataSetChanged();
            if (brandAdapter.getItemCount() == 0) {
                // If the adapter has no items, show the TextView
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                // If the adapter has items, hide the TextView
                emptyTextView.setVisibility(View.GONE);
            }
            brandAdapter.notifyDataSetChanged();
            LoadBrandProducts(brands.get(0).getBrand_code());
            brandAdapter.setOnClickListener(new BrandAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Brand brand) {
                    LoadBrandProducts(brands.get(position).getBrand_code());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}