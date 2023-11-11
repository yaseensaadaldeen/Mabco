package com.example.mabco.ui.Product;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mabco.Adapters.ProductSpecsAdapter;
import com.example.mabco.Classes.Product;
import com.example.mabco.Classes.ProductSpecs;
import com.example.mabco.R;
import com.example.mabco.databinding.FragmentProductDetailesBinding;
import com.example.mabco.databinding.FragmentProductSpecsBinding;

import java.util.ArrayList;

public class ProductSpecsFragment extends Fragment {
    private FragmentProductSpecsBinding Specsbinding;
    public ProductSpecsAdapter productSpecsAdapter;
    public RecyclerView DetailsRecycleview;
    public ArrayList<ProductSpecs> productSpecs;
    public Product product;
    public TextView product_desc;
    public Context context;

    public ProductSpecsFragment(ArrayList<ProductSpecs> productSpecs, Product product) {
        this.productSpecs = productSpecs;
        this.product = product;
    }

    public ProductSpecsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Specsbinding = FragmentProductSpecsBinding.inflate(inflater, container, false);
        context = this .getContext();
        product_desc = Specsbinding.productDesc;
        DetailsRecycleview = Specsbinding.Specs;
        product_desc.setText(product.getStk_desc());
        productSpecsAdapter = new ProductSpecsAdapter(productSpecs, context);
        DetailsRecycleview.setAdapter(productSpecsAdapter);//.notifyDataSetChanged();
        productSpecsAdapter.notifyDataSetChanged();
        return Specsbinding.getRoot();
    }
}