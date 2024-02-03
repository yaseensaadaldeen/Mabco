package com.example.mabco.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mabco.Classes.Offer;
import com.example.mabco.Classes.ProductColor;
import com.example.mabco.databinding.ProductOfferItemBinding;

import java.util.ArrayList;

public class OfferRecyclerViewAdapter extends RecyclerView.Adapter<OfferRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Offer> productOffers;
    private Context context;
    public ProductColor selectedColor;
    private int selectedItem;
    private int selectedPos = RecyclerView.NO_POSITION;
    private OfferRecyclerViewAdapter.OnClickListener onClickListener;

    public void setOnClickListener(OfferRecyclerViewAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OfferRecyclerViewAdapter(ArrayList<Offer> productOffers, Context context) {
        this.productOffers = productOffers;
        this.context = context;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(ProductOfferItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.productOffer = productOffers.get(position);
        Glide.with(context).load("https://mabcoonline.com/" + holder.productOffer.getOffer_image_url()).fitCenter().into(holder.offerImage);
        holder.offerDesc.setText(holder.productOffer.getOffer_title());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (onClickListener != null) {

                        onClickListener.onClick(position,holder.productOffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productOffers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView offerImage;
        public final TextView offerDesc;
        public Offer productOffer;

        public ViewHolder(ProductOfferItemBinding binding) {
            super(binding.getRoot());
            offerImage = binding.offerImage;
            offerDesc = binding.offerDesc;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + offerDesc.getText() + "'";
        }
    }

    public interface OnClickListener {
        void onClick(int position, Offer offer);
    }
}