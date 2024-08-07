package com.mabcoApp.mabco.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.Classes.ProductSpecs;
import com.mabcoApp.mabco.ui.Product.FAQFragment;
import com.mabcoApp.mabco.ui.Product.ProductOfferFragment;
import com.mabcoApp.mabco.ui.Product.ProductSpecsFragment;

import java.util.ArrayList;

public class ProductDetailsAdapter extends FragmentStateAdapter {
    public ArrayList<ProductSpecs> productSpecs;
    public ArrayList<Offer> productoffer;
    public Product product;

    public ArrayList<ProductSpecs> getProductSpecs() {
        return productSpecs;
    }

    public ArrayList<Offer> getProductoffer() {
        return productoffer;
    }

    public void setProductoffer(ArrayList<Offer> productoffer) {
        this.productoffer = productoffer;
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
                if (product.getCategoryModel().getCat_code().equals("09"))
                {
                    return new FAQFragment(product);
                }
                else
                {
                return new ProductOfferFragment( productoffer, product);
                }
            case 2:
                return new ProductOfferFragment( productoffer, product);

        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

