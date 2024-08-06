package com.mabcoApp.mabco.ui.Product;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabcoApp.mabco.Adapters.OfferAdapter;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.R;

import java.util.ArrayList;

public class ProductOfferFragment extends Fragment {
    private int mColumnCount = 2;
    private ArrayList<Offer> productOffer;
    Context context;
    OfferAdapter offerRecyclerViewAdapter;
    Product product;

    public ProductOfferFragment() {
    }

    public ProductOfferFragment(ArrayList<Offer> productOffer,Product product) {
        this.productOffer = productOffer;
        this.product = product;
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
                offerRecyclerViewAdapter = new OfferAdapter(context,productOffer );
                recyclerView.setAdapter(offerRecyclerViewAdapter);
                offerRecyclerViewAdapter.setOnClickListener(new OfferAdapter.OnClickListener() {
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
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        OfferProductDialog listDialog = new OfferProductDialog(context, offer,navController,"ProductDetailsFragment",product.getStk_code()) {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        };
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(listDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1700;
        listDialog.show();
        listDialog.getWindow().setAttributes(lp);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }
}