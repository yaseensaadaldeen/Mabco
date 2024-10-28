package com.mabcoApp.mabco.ui.Product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mabcoApp.mabco.Adapters.CategoryProductsAdapter;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.ShoppingCart;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.databinding.FragmentProductsBinding;
import com.varunest.sparkbutton.SparkButton;

public class ProductsFragment extends Fragment {

    private FragmentProductsBinding productsBinding;
    Context context;
    SparkButton shopping_cart_btn;
    NavController navController;
    int shoppingcartItems;
    SearchView Edtxt_search;
    FloatingActionButton btn_back, compareButton;
    private TabLayout category_tab_layout;
    private ViewPager2 category_products_pager;
    CategoryProductsAdapter categoryProductsAdapter;
    TextView txt_items_count,compare_notify;
    String Destenation = "";
    int selectedTab = 0;
    SharedPreferences FilterPreference;
    double maxShelfPrice = 0;
    double minShelfPrice = 0;

    public ProductsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"NotifyDataSetChanged", "SuspiciousIndentation"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            productsBinding = FragmentProductsBinding.inflate(inflater, container, false);
            context = getContext();
            shopping_cart_btn = productsBinding.shoppingCartBtn;
            Edtxt_search = productsBinding.categoryProductsSearch;
            btn_back = productsBinding.backBtn;
            category_tab_layout = productsBinding.categoryTabLayout;
            category_products_pager = productsBinding.categoryProductsPager;
            compareButton=productsBinding.compareButton;
            txt_items_count = productsBinding.txtItemsCount;
            compare_notify=productsBinding.compareNotify;
            FilterPreference = context.getSharedPreferences("FilterData", Context.MODE_PRIVATE);
            final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            navController = navHostFragment.getNavController();
            if (ShoppingCart.getItemCount(context) == 0) txt_items_count.setVisibility(View.GONE);
            else txt_items_count.setVisibility(View.VISIBLE);
            compareButton.setOnClickListener(v->{
                navController.navigate(R.id.action_productsFragment_to_nav_compare);
            });
            hide();

            int productCount = Product.countComparedProducts(context);
            boolean notify = productCount > 0;
            compare_notify.setVisibility(notify ? View.VISIBLE : View.GONE);
            txt_items_count.setText(String.valueOf(ShoppingCart.getItemCount(context)));
            btn_back.setOnClickListener(v -> getActivity().onBackPressed());
            categoryProductsAdapter = new CategoryProductsAdapter(this);
            category_products_pager.setAdapter(categoryProductsAdapter);
            category_products_pager.setUserInputEnabled(false);
            category_tab_layout.addTab(category_tab_layout.newTab().setText(getString(R.string.Mobiles)), 0);
            category_tab_layout.addTab(category_tab_layout.newTab().setText(getString(R.string.power)), 1);
            category_tab_layout.addTab(category_tab_layout.newTab().setText(getString(R.string.Mobile_acc)), 2);
            category_tab_layout.addTab(category_tab_layout.newTab().setText(getString(R.string.spare)), 3);


            Resources standardResources = context.getResources();
            Configuration config = new Configuration(standardResources.getConfiguration());
            if (config.getLayoutDirection() == Layout.DIR_LEFT_TO_RIGHT) {
                btn_back.setImageResource(R.drawable.back_rtl);
            }
            shopping_cart_btn.setOnClickListener(v -> {
                navController.navigate(R.id.action_productsFragment_to_shoppingCartFragment);
            });
            category_tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    SharedPreferences viewpager2 = context.getSharedPreferences("viewpager2", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = viewpager2.edit();
                    editor.putInt("position", tab.getPosition());
                    editor.apply();
                    category_products_pager.setCurrentItem(tab.getPosition());
                    selectedTab = tab.getPosition();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            category_products_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    SharedPreferences viewpager2 = context.getSharedPreferences("viewpager2", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = viewpager2.edit();
                    editor.putInt("position", position);
                    editor.apply();
                    category_tab_layout.selectTab(category_tab_layout.getTabAt(position));
                }
            });
            categoryProductsAdapter.notifyDataSetChanged();
            Edtxt_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Save search and reload the history

                    categoryProductsAdapter.setSearchtext(String.valueOf(Edtxt_search.getQuery()));
                    category_products_pager.setAdapter(categoryProductsAdapter);
                    categoryProductsAdapter.notifyDataSetChanged();
                    category_products_pager.setCurrentItem(selectedTab);

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (newText.isEmpty()) {
                        categoryProductsAdapter.setSearchtext(String.valueOf(Edtxt_search.getQuery()));
                        category_products_pager.setAdapter(categoryProductsAdapter);
                        categoryProductsAdapter.notifyDataSetChanged();
                        category_products_pager.setCurrentItem(selectedTab);
                    }
                    return false;
                }
            });
            Toast.makeText(getContext(), getString(R.string.long_press_compare), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return productsBinding.getRoot();
    }

    public void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
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

        try {
            super.onDestroyView();
            NavDestination currentDestination = navController.getCurrentDestination();
            if (currentDestination != null && (currentDestination.getId() != R.id.productDetailsFragment)) {
                //show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}