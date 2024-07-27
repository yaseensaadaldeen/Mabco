package com.example.mabco.ui.Compare;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.mabco.Classes.Product;
import com.example.mabco.R;
import com.example.mabco.databinding.FragmentCompareBinding;

import java.util.ArrayList;
import java.util.List;

public class CompareFragment extends Fragment {
    Context context;
    FragmentCompareBinding fragmentCompareBindings;
    RelativeLayout product1_add_layout,product2_add_layout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentCompareBindings = FragmentCompareBinding.inflate(inflater, container, false);
        context=getContext();
        product1_add_layout.setOnClickListener(v->{
            showProductSelectionDialog();
        });
        product2_add_layout.setOnClickListener(v->{
            showProductSelectionDialog();
        });
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
        Spinner spinnerProduct = dialogView.findViewById(R.id.product_spinner);

        // Dummy data for categories and products
        List<String> categories = new ArrayList<>();
        categories.add("Electronics");
        categories.add("Books");
        categories.add("Clothing");

        List<String> products = new ArrayList<>();
        products.add("Product 1");
        products.add("Product 2");
        products.add("Product 3");

        // Set up the adapters for the spinners
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, products);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(productAdapter);
        Product product = null;


        AlertDialog dialog = builder.create();
        cancle.setOnClickListener(v -> {
            dialog.dismiss();
        });
        add.setOnClickListener(v -> {
            Product.AddProductToComparison(context, product);
        });
        dialog.show();
    }
}