package com.mabcoApp.mabco.ui.Product;

import static com.mabcoApp.mabco.Classes.Product.filterProducts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.RangeSlider;
import com.mabcoApp.mabco.Adapters.ProductsAdapter;
import com.mabcoApp.mabco.Classes.CategoryModel;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private LinearLayout savedSearchesContainer;
    SharedPreferences preferences, PersonalPreference, sharedPreferences, FilterPreference;

    public RequestQueue requestQueue;
    public ArrayList<Product> products;
    private static final String PREFS_NAME = "SearchHistoryPrefs";
    private static final String SEARCH_HISTORY_KEY = "search_his";
    public ShimmerFrameLayout search_shimmer_view_containers;
    Context context;
    private ArrayList<Product> searchproducts;
    String local;
    private ProductsAdapter productsAdapter;

    private RecyclerView SearchProductsRecyler;
    NavController navController;
    ImageView FilterButton;
    double maxShelfPrice = 0;
    double minShelfPrice = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI components
        searchView = view.findViewById(R.id.searchView);
        savedSearchesContainer = view.findViewById(R.id.savedSearchesContainer);
        ImageView backButton = view.findViewById(R.id.backButton);
        FilterButton = view.findViewById(R.id.FilterButton);
        context = view.getContext();
        // Initialize SharedPreferences
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        FilterPreference = context.getSharedPreferences("FilterData", Context.MODE_PRIVATE);
        final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();
        SearchProductsRecyler = view.findViewById(R.id.search_products);  // Replace with your RecyclerView ID
        SearchProductsRecyler.setLayoutManager(new LinearLayoutManager(getContext()));
        search_shimmer_view_containers = view.findViewById(R.id.search_shimmer_view_containers);
        search_shimmer_view_containers.setVisibility(View.GONE);
        local = PersonalPreference.getString("Language", "ar");
        // Load saved searches
        loadSavedSearches();
        toggleVisibility();
        FilterButton.setOnClickListener(v -> showFilterDialog());
        FilterButton.setVisibility(View.GONE);
        // Set up SearchView functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Save search and reload the history

                SearchProductsAPI(query, haveNetworkConnection());
                clearFilterPreferences();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Back button functionality
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed(); // Go back to the previous screen
            }
        });
        return view;
    }

    // Save search query to SharedPreferences
    private void saveSearch(String query) {
        Set<String> searchHistory = sharedPreferences.getStringSet(SEARCH_HISTORY_KEY, new HashSet<>());
        searchHistory.add(query);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(SEARCH_HISTORY_KEY, searchHistory);
        editor.apply();

        // Reload the saved searches
        loadSavedSearches();
    }

    // Load saved searches and display them
    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadSavedSearches() {
        savedSearchesContainer.removeAllViews(); // Clear previous views
        Set<String> searchHistory = sharedPreferences.getStringSet(SEARCH_HISTORY_KEY, new HashSet<>());

        for (String search : searchHistory) {
            TextView searchTextView = new TextView(getContext());
            searchTextView.setText(search);
            searchTextView.setTextSize(16f);
            searchTextView.setBackground(getResources().getDrawable(R.drawable.input_background));
            searchTextView.setTextColor(getResources().getColor(android.R.color.black));
            searchTextView.setPadding(10, 10, 10, 10);

            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "You searched for: " + search, Toast.LENGTH_SHORT).show();
                    searchView.setQuery(search, true);

                    SearchProductsAPI(search, haveNetworkConnection());
                }
            });

            savedSearchesContainer.addView(searchTextView);
        }
    }

    public void toggleVisibility() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }


    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void SearchProductsAPI(String query, boolean online) {
        saveSearch(query);
        if (online) {
            search_shimmer_view_containers.setVisibility(View.VISIBLE);
            search_shimmer_view_containers.startShimmer();
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
            String UserID = Token.getString("UserID", "");
            String url = UrlEndPoint.General + "Service1.svc/searchNew/" + query;
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("SearchProducts-" + query, response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("searchResult");
                        if (array != null) {
                            LoadProducts(array);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    SearchProductsAPI(query, false);
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
            String ProductsWithDiscount = preferences.getString("SearchProducts-" + query, "");
            if (!ProductsWithDiscount.equals("")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(ProductsWithDiscount);
                    JSONArray array = jsonResponse.optJSONArray("searchResult");
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

                });
                alertDialog.setCancelable(true);
                alertDialog.create().show();
            }
        }
    }

    private void LoadProducts(JSONArray array) {
        try {
            searchproducts = new ArrayList<Product>();
            maxShelfPrice = Double.MIN_VALUE;
            minShelfPrice = Double.MAX_VALUE;

            for (int i = 0; i < array.length(); i++) {
                JSONObject arrayObj = array.getJSONObject(i);
                Product product = new Product(arrayObj.optString("stk_code"), arrayObj.optString("device_title"), arrayObj.optString("stk_desc"), arrayObj.optString("shelf_price"), new CategoryModel(arrayObj.optString("cat_code")), arrayObj.optString("discount"), arrayObj.optString("coupon"), arrayObj.optString("tag"), "https://" + arrayObj.optString("image_link"));

                // Parse shelf price as a double
                double shelfPrice = Double.parseDouble(product.getShelf_price()); // Ensure getShelfPrice() returns a string

                // Update max and min prices
                if (shelfPrice > maxShelfPrice) {
                    maxShelfPrice = shelfPrice;
                }
                if (shelfPrice < minShelfPrice) {
                    minShelfPrice = shelfPrice;
                }
                searchproducts.add(product);
            }
            if (searchproducts.size() == 0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("No items to show");
                alertDialog.setTitle("Sorry");
                search_shimmer_view_containers.stopShimmer();
                search_shimmer_view_containers.setVisibility(View.GONE);
                alertDialog.setPositiveButton("OK", (dialog, which) -> {
                });
                alertDialog.setCancelable(true);
                alertDialog.create().show();

            } else {
                FilterButton.setVisibility(View.VISIBLE);
                search_shimmer_view_containers.stopShimmer();
                search_shimmer_view_containers.setVisibility(View.GONE);
                hideKeyboard(requireActivity());
                productsAdapter = new ProductsAdapter(context, searchproducts, local);
                SearchProductsRecyler.setAdapter(productsAdapter);//.notifyDataSetChanged();
                productsAdapter.setOnClickListener(new ProductsAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, Product product) {//there where i want to navigate to the productdetalesfragment passing the product to it
                        navController.navigate((NavDirections) SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(product));
                    }
                });
                productsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View filterView = inflater.inflate(R.layout.filter_dialog, null);

        RangeSlider priceRangeSlider = filterView.findViewById(R.id.priceRangeSlider);
        EditText minPriceInput = filterView.findViewById(R.id.minPriceInput);
        EditText maxPriceInput = filterView.findViewById(R.id.maxPriceInput);
        LinearLayout categoryFilterContainer = filterView.findViewById(R.id.categoryFilterContainer);

        // Load saved filter state from SharedPreferences
        float savedMinPrice = FilterPreference.getFloat("min_price", (float) minShelfPrice);
        float savedMaxPrice = FilterPreference.getFloat("max_price", (float) maxShelfPrice);
        Set<String> selectedCategoriesSet = FilterPreference.getStringSet("selected_categories", new HashSet<>());

        // Set price slider values from saved state or default
        priceRangeSlider.setValueFrom((float) minShelfPrice);
        priceRangeSlider.setValueTo((float) maxShelfPrice);
        priceRangeSlider.setValues(savedMinPrice, savedMaxPrice);
        minPriceInput.setText(String.valueOf(savedMinPrice));
        maxPriceInput.setText(String.valueOf(savedMaxPrice));

        // Populate the category filter section with checkboxes
        CategoryModel[] categoryModels = new CategoryModel[]{
                new CategoryModel(getString(R.string.Mobiles), "00", R.drawable.phone_category),
                new CategoryModel(getString(R.string.power), "09", R.drawable.battaries_category),
                new CategoryModel(getString(R.string.Mobile_acc), "01", R.drawable.accessories_category),
                new CategoryModel(getString(R.string.spare), "02", R.drawable.maintanence_category)};
//        new CategoryModel(getString(R.string.Gaming), "07", R.drawable.games_categorty)
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (fromUser) {
                minPriceInput.setText(String.valueOf(Math.round(values.get(0)))); // update min value
                maxPriceInput.setText(String.valueOf(Math.round(values.get(1)))); // update max value
            }
        });
        List<CheckBox> checkBoxList = new ArrayList<>();
        for (CategoryModel category : categoryModels) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(category.getTitle());

            // Check if this category was selected previously
            if (selectedCategoriesSet.contains(category.getCat_code())) {
                checkBox.setChecked(true);
            }

            categoryFilterContainer.addView(checkBox);
            checkBoxList.add(checkBox);
        }

        // Add logic to apply the filter when the user clicks "Apply"
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter Products")
                .setView(filterView)
                .setPositiveButton("Apply", (dialog, which) -> {
                    float minPrice = priceRangeSlider.getValues().get(0);
                    float maxPrice = priceRangeSlider.getValues().get(1);

                    // Collect selected categories
                    List<CategoryModel> selectedCategoryModels = new ArrayList<>();
                    for (int i = 0; i < checkBoxList.size(); i++) {
                        if (checkBoxList.get(i).isChecked()) {
                            selectedCategoryModels.add(categoryModels[i]);
                        }
                    }

                    // Apply filters
                    applyFilters(minPrice, maxPrice, selectedCategoryModels);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void applyFilters(float minPrice, float maxPrice, List<CategoryModel> selectedCategories) {
        ArrayList<Product> filteredProducts = filterProducts(searchproducts, minPrice, maxPrice, selectedCategories);

        // Save filter state to SharedPreferences
        SharedPreferences.Editor editor = FilterPreference.edit();
        editor.putFloat("min_price", minPrice);
        editor.putFloat("max_price", maxPrice);

        // Save selected categories
        Set<String> selectedCategoriesSet = new HashSet<>();
        for (CategoryModel category : selectedCategories) {
            selectedCategoriesSet.add(category.getCat_code()); // Save category code
        }
        editor.putStringSet("selected_categories", selectedCategoriesSet);
        editor.apply();

        // Update UI with filtered products
        productsAdapter = new ProductsAdapter(context, filteredProducts, local);
        SearchProductsRecyler.setAdapter(productsAdapter);
        productsAdapter.setOnClickListener(new ProductsAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Product product) {//there where i want to navigate to the productdetalesfragment passing the product to it
                navController.navigate((NavDirections) SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(product));
            }
        });
        productsAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearFilterPreferences();
    }
    private void clearFilterPreferences() {
        SharedPreferences.Editor editor = FilterPreference.edit();
        editor.clear();  // Clear all the saved preferences in sharedPreferences
        editor.apply();  // Apply changes
    }
}