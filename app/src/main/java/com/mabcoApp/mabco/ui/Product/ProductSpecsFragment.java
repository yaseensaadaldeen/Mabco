package com.mabcoApp.mabco.ui.Product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mabcoApp.mabco.Adapters.ProductSpecsAdapter;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.ProductSpecs;
import com.mabcoApp.mabco.databinding.FragmentProductSpecsBinding;

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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Specsbinding = FragmentProductSpecsBinding.inflate(inflater, container, false);
        ShimmerFrameLayout shimmerFrameLayout = Specsbinding.shimmiringSpec;
        ShimmerFrameLayout shimmiringSpecs= Specsbinding.shimmiringSpecs;

        context = this.getContext();
        try {
            if (productSpecs != null) {
                product_desc = Specsbinding.productDesc;
                DetailsRecycleview = Specsbinding.Specs;
                shimmerFrameLayout.stopShimmer();
                shimmiringSpecs.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                shimmiringSpecs.setVisibility(View.GONE);
                product_desc.setText(product.getStk_desc());
                productSpecsAdapter = new ProductSpecsAdapter(productSpecs, context);
                DetailsRecycleview.setAdapter(productSpecsAdapter);//.notifyDataSetChanged();
                productSpecsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Specsbinding.getRoot();
    }
}