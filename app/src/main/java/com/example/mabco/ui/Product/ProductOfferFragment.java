package com.example.mabco.ui.Product;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mabco.Adapters.OfferRecyclerViewAdapter;
import com.example.mabco.Classes.Offer;
import com.example.mabco.R;
import com.example.mabco.ui.Product.placeholder.PlaceholderContent;

import java.util.ArrayList;

public class ProductOfferFragment extends Fragment {
    private int mColumnCount = 2;
    private ArrayList<Offer> productOffer;

    public ProductOfferFragment() {
    }

    public ProductOfferFragment(ArrayList<Offer> productOffer) {
        this.productOffer = productOffer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_offer_list, container, false);
        try {
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                if (productOffer.size() == 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                }
                recyclerView.setAdapter(new OfferRecyclerViewAdapter(productOffer, context));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;

    }
}