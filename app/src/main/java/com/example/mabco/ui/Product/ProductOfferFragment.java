package com.example.mabco.ui.Product;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.mabco.Adapters.OfferRecyclerViewAdapter;
import com.example.mabco.Classes.Offer;
import com.example.mabco.R;

import java.util.ArrayList;

public class ProductOfferFragment extends Fragment {
    private int mColumnCount = 2;
    private ArrayList<Offer> productOffer;
    Context context;
    OfferRecyclerViewAdapter offerRecyclerViewAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_offer_list, container, false);
        try {
            if (view instanceof RecyclerView) {
               
                context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                offerRecyclerViewAdapter = new OfferRecyclerViewAdapter(productOffer, context);
                recyclerView.setAdapter(offerRecyclerViewAdapter);
                offerRecyclerViewAdapter.setOnClickListener(new OfferRecyclerViewAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position, Offer offer) {
                        openDialog(offer);

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;

    }

    public void openDialog(Offer offer) {

        OfferProductDialog listDialog = new OfferProductDialog(context, offer) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        };
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(listDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1400;
        listDialog.show();
        listDialog.getWindow().setAttributes(lp);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }
}