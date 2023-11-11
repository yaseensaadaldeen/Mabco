package com.example.mabco.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mabco.Classes.Product;
import com.example.mabco.Classes.ProductSpecs;
import com.example.mabco.ui.Product.FAQFragment;
import com.example.mabco.ui.Product.ProductSpecsFragment;

import java.util.ArrayList;

public class ProductDetailsAdapter extends FragmentStateAdapter {
    public ArrayList<ProductSpecs> productSpecs;
    public Product product;

    public ArrayList<ProductSpecs> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(ArrayList<ProductSpecs> productSpecs) {
        this.productSpecs = productSpecs;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductDetailsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ProductDetailsAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ProductDetailsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProductSpecsFragment(productSpecs, product);
            case 1:
            case 2:
                return new FAQFragment();
        }
        return new ProductSpecsFragment(productSpecs, product);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}

