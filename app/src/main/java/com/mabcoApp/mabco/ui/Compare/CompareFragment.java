package com.mabcoApp.mabco.ui.Compare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.bumptech.glide.Glide;
import com.mabcoApp.mabco.Adapters.ProductSpecsAdapter;
import com.mabcoApp.mabco.Adapters.ProductSpinnerAdapter;
import com.mabcoApp.mabco.Classes.CategoryModel;
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.ProductColor;
import com.mabcoApp.mabco.Classes.ProductSpecs;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.databinding.FragmentCompareBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CompareFragment extends Fragment {
    Context context;
    FragmentCompareBinding fragmentCompareBindings;
    RelativeLayout product1_add_layout, product2_add_layout, product_layout, product1_data_layout, product2_data_layout, product1Layout, product2Layout;
    FloatingActionButton product1_add_btn, product2_add_btn, product1_delete, product2_delete, product1_buy, product2_buy;
    public RequestQueue requestQueue;
    SharedPreferences preferences, SharedCategoryProductsData;
    ProductSpecsAdapter productSpecsAdapter;
    public ArrayList<Product> products;
    public CategoryModel selectedCategory;
    public Product selectedProduct;
    public Spinner spinnerProduct;
    public ArrayList<ProductSpecs> productSpecs;
    public ArrayList<ProductColor> productColors;
    ShimmerFrameLayout shimmiring_spinner, product1ImageViewContainer, product2ImageViewContainer, product2SpecShimmerViewContainer, product1SpecShimmerViewContainer;
    private static final String PREF_NAME = "ProductCompare";
    private static final String PRODUCT1_KEY = "product1";
    private static final String PRODUCT2_KEY = "product2";
    ImageView productImage1, productImage2;
    RecyclerView product1_spec, product2_spec;


    // Access the second included layout

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentCompareBindings = FragmentCompareBinding.inflate(inflater, container, false);
        context = getContext();
        product1_add_layout = fragmentCompareBindings.product1AddLayout;
        product2_add_layout = fragmentCompareBindings.product2AddLayout;
        product1_data_layout = fragmentCompareBindings.product1DataLayout;
        product2_data_layout = fragmentCompareBindings.product2DataLayout;
        product1_add_btn = fragmentCompareBindings.product1AddBtn;
        product2_add_btn = fragmentCompareBindings.product2AddBtn;
        SharedCategoryProductsData = context.getSharedPreferences("CategoryProductsCompareData", Context.MODE_PRIVATE);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE) showNavigationBar();
        product1_add_btn.setOnClickListener(v -> {
            showProductSelectionDialog();
        });
        product2_add_btn.setOnClickListener(v -> {
            showProductSelectionDialog();
        });

        Start_comparison();
        preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
        return fragmentCompareBindings.getRoot();
    }

    private void showProductSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.compare_product_selection, null);
        builder.setView(dialogView);


        Button add = dialogView.findViewById(R.id.btn_Add);
        Button cancle = dialogView.findViewById(R.id.btn_cancel);
        Spinner spinnerCategory = dialogView.findViewById(R.id.product_cat_spinner);
        spinnerProduct = dialogView.findViewById(R.id.product_spinner);
        FloatingActionButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        product_layout = dialogView.findViewById(R.id.product_layout);
        shimmiring_spinner = dialogView.findViewById(R.id.shimmiring_spinner);

        // Create some sample categories
        ArrayList<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel(getString(R.string.Mobiles), "00", R.drawable.phone_category));
        categories.add(new CategoryModel(getString(R.string.power), "09", R.drawable.battaries_category));

        // Initialize adapter for categories
        ProductSpinnerAdapter categoryAdapter = new ProductSpinnerAdapter(context, categories, false);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (CategoryModel) spinnerCategory.getSelectedItem();
                product_layout.setVisibility(View.VISIBLE);
                spinnerProduct.setVisibility(View.GONE);
                shimmiring_spinner.startShimmer();
                shimmiring_spinner.setVisibility(View.VISIBLE);
                boolean online;
                if (selectedCategory != null) {
                    if (isDataFresh(context, selectedCategory.getCat_code())) online = false;
                    else online = NetworkStatus.isOnline(context);
                    BrandProductsAPI(context, online, selectedCategory.getCat_code());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        spinnerProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProduct = (Product) spinnerProduct.getSelectedItem();
                //   ProductDetailsAPI(context , NetworkStatus.isOnline(context),selectedProduct)
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        AlertDialog dialog = builder.create();

        add.setOnClickListener(v -> {
            if (selectedProduct != null) {
                Product.AddProductToComparison(context, selectedProduct);

                int productCount = Product.countComparedProducts(context);
                boolean notify = productCount > 0;
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.AddToolbarNotification(productCount, R.id.nav_compare, notify);
                }
                dialog.dismiss();
                Start_comparison();

            } else
                Toast.makeText(context, getString(R.string.please_select_product) + selectedCategory.getTitle(), Toast.LENGTH_SHORT).show();
        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        cancle.setOnClickListener(v -> {
            dialog.dismiss();
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1700;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void Start_comparison() {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String product1Json = sharedPreferences.getString(PRODUCT1_KEY, null);
        String product2Json = sharedPreferences.getString(PRODUCT2_KEY, null);

        Product product1 = Product.fromJson(product1Json);
        Product product2 = Product.fromJson(product2Json);

        // Access the first included layout
        product1Layout = fragmentCompareBindings.product1DataLayout;
        productImage1 = product1Layout.findViewById(R.id.product1_image);
        product1ImageViewContainer = product1Layout.findViewById(R.id.Product1ImageViewContainer);
        product1_delete = product1Layout.findViewById(R.id.product1_delete);
        product1SpecShimmerViewContainer = product1Layout.findViewById(R.id.SpecShimmerViewContainer);
        product1_spec = product1Layout.findViewById(R.id.product1_spec);
        product1_buy = product1Layout.findViewById(R.id.product1_buy);

        // Access the second included layout
        product2Layout = fragmentCompareBindings.product2DataLayout;
        productImage2 = product2Layout.findViewById(R.id.product1_image);
        product2ImageViewContainer = product2Layout.findViewById(R.id.Product1ImageViewContainer);
        product2_delete = product2Layout.findViewById(R.id.product1_delete);
        product2SpecShimmerViewContainer = product2Layout.findViewById(R.id.SpecShimmerViewContainer);
        product2_spec = product2Layout.findViewById(R.id.product1_spec);
        product2_buy = product2Layout.findViewById(R.id.product1_buy);

        // Fill product data
        fillProductData(product1Layout, productImage1, product1ImageViewContainer, product1_delete, product1_buy, product1SpecShimmerViewContainer, product1_spec, product1, 1);
        fillProductData(product2Layout, productImage2, product2ImageViewContainer, product2_delete, product2_buy, product2SpecShimmerViewContainer, product2_spec, product2, 2);
    }

    @SuppressLint("SetTextI18n")
    private void fillProductData(RelativeLayout productLayout, ImageView productImageView, ShimmerFrameLayout shimmerFrameLayout, FloatingActionButton product_delete, FloatingActionButton product_buy, ShimmerFrameLayout productSpecShimmerViewContainer, RecyclerView product_spec, Product product, int position) {
        TextView productName = productLayout.findViewById(R.id.product1_name);
        TextView productPrice = productLayout.findViewById(R.id.product1_price);
        if (product != null) {
            if (position == 1) {
                product1_add_layout.setVisibility(View.GONE);
                product1_data_layout.setVisibility(View.VISIBLE);
            } else {
                product2_add_layout.setVisibility(View.GONE);
                product2_data_layout.setVisibility(View.VISIBLE);
            }
            ProductDetailsAPI(context, NetworkStatus.isOnline(context), product, product_spec, productSpecShimmerViewContainer);

            productName.setText(product.getProduct_title());
            productPrice.setText(product.getShelf_price() + getString(R.string.sp));
            productSpecShimmerViewContainer.startShimmer();
            productSpecShimmerViewContainer.setVisibility(View.VISIBLE);
            // Start shimmer animation
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            product_delete.setOnClickListener(v -> {
                Product.RemoveProductFromComparison(context, product);
                int productCount = Product.countComparedProducts(context);
                boolean notify = productCount > 0;
                if (getActivity() instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.AddToolbarNotification(productCount, R.id.nav_compare, notify);
                }
                Start_comparison();
            });
            product_buy.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(fragmentCompareBindings.getRoot());
                NavDirections action = CompareFragmentDirections.actionNavCompareToProductDetailsFragment(product);
                Navigation.findNavController(fragmentCompareBindings.getRoot()).navigate(action);
            });
            // Load image using Glide
            Glide.with(this).load(product.getProduct_image()) // URL or resource ID
                    .fitCenter().into(productImageView);
        } else {
            if (position == 1) {
                product1_add_layout.setVisibility(View.VISIBLE);
                product1_data_layout.setVisibility(View.GONE);
            } else {
                product2_add_layout.setVisibility(View.VISIBLE);
                product2_data_layout.setVisibility(View.GONE);
            }

        }
    }

    public void BrandProductsAPI(Context context, boolean isOnline, String cat_code) {
        if (isOnline) {
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();

            String url = UrlEndPoint.General + "Service1.svc/getStocksByCat/" + cat_code;
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray BrandsArray = jsonResponse.optJSONArray("getBrands");
                        JSONArray ProductsArray = jsonResponse.optJSONArray("getStocks");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String currentDateAndTime = sdf.format(new Date());
                        SharedPreferences.Editor editor = SharedCategoryProductsData.edit();
                        editor.putString("CategoryBrandsProducts" + cat_code, response);
                        editor.putString("CategoryBrandsProductsDate" + cat_code, currentDateAndTime);
                        editor.apply();
                        if (ProductsArray != null) {
                            LoadBrands(BrandsArray, ProductsArray);
                            ProductSpinnerAdapter productAdapter = new ProductSpinnerAdapter(context, products, true);
                            spinnerProduct.setAdapter(productAdapter);
                            shimmiring_spinner.stopShimmer();
                            shimmiring_spinner.setVisibility(View.GONE);
                            spinnerProduct.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    BrandProductsAPI(context,false,cat_code);
                }
            }){

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
                ProductSpinnerAdapter productAdapter = new ProductSpinnerAdapter(context, products, true);
                spinnerProduct.setAdapter(productAdapter);
                shimmiring_spinner.stopShimmer();
                shimmiring_spinner.setVisibility(View.GONE);
                spinnerProduct.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void LoadBrands(JSONArray BrandsArray, JSONArray ProductsArray) {
        try {
            products = new ArrayList<>();
            for (int j = 0; j < BrandsArray.length(); j++) {
                JSONObject BrandOBJ = BrandsArray.getJSONObject(j);
                try {
                    for (int i = 0; i < ProductsArray.length(); i++) {
                        JSONObject ProductOBJ = ProductsArray.getJSONObject(i);
                        Product product = new Product();
                        product = new Product(ProductOBJ.optString("stk_code"), ProductOBJ.optString("device_title"), ProductOBJ.optString("stk_desc"), ProductOBJ.optString("shelf_price"), new CategoryModel(selectedCategory.getCat_code()), "0", "0", "", "https://" + ProductOBJ.optString("image_link"));
                        products.add(product);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDataFresh(Context context, String cat_code) {
        String storedDateStr = SharedCategoryProductsData.getString("CategoryBrandsProductsDate" + cat_code, null);
        if (storedDateStr == null) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date storedDate = sdf.parse(storedDateStr);
            Date currentDate = new Date();
            long diffInMillis = currentDate.getTime() - storedDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            return diffInDays <= 1;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void ProductDetailsAPI(Context context, boolean online, Product product, RecyclerView product_spec_rec, ShimmerFrameLayout productSpecShimmerViewContainer) {
        requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String UserID = Token.getString("UserID", "");
        String url = UrlEndPoint.General + "Service1.svc/getStockDetails/" + product.getStk_code() + ",AR,1,"+UserID ;
        if (online) {
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("ProductDetails" + product.getStk_code(), response);
                        editor.putString("ProductOffers" + product.getStk_code(), response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray DetailsArray = jsonResponse.optJSONArray("getStockDetailsResult");
                        LoadProductDetails(DetailsArray, product_spec_rec, productSpecShimmerViewContainer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    ProductDetailsAPI(context , false,product,product_spec_rec,productSpecShimmerViewContainer);
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
            String ProductDetails = preferences.getString("ProductDetails" + product.getStk_code(), "");
            String ProductOffers = preferences.getString("ProductDetails" + product.getStk_code(), "");
            if (!ProductDetails.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(ProductDetails);
                    JSONArray DetailsArray = jsonResponse.optJSONArray("getStockDetailsResult");
                    LoadProductDetails(DetailsArray, product_spec_rec, productSpecShimmerViewContainer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (!ProductOffers.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(ProductOffers);
                    JSONArray offersArray = jsonResponse.optJSONArray("getStockOffers");
                    // LoadOffers(offersArray);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void LoadProductDetails(JSONArray DetailsArray, RecyclerView product_spec_rec, ShimmerFrameLayout productSpecShimmerViewContainer) {
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
            productSpecShimmerViewContainer.stopShimmer();
            productSpecShimmerViewContainer.setVisibility(View.GONE);
            productSpecsAdapter = new ProductSpecsAdapter(productSpecs, context, true);
            product_spec_rec.setAdapter(productSpecsAdapter);//.notifyDataSetChanged();
            product_spec_rec.setNestedScrollingEnabled(false);
            productSpecsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}