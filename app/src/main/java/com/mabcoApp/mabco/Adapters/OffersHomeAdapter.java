package com.mabcoApp.mabco.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.R;

public class OffersHomeAdapter extends RecyclerView.Adapter<OffersHomeAdapter.ViewHolder> {
    private Context context;
    public Offer[] offers;

    public OffersHomeAdapter(Context context, Offer[] offers) {
        this.context = context;
        this.offers = offers;
    }

    @NonNull
    @Override
    public OffersHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.offer_home_item,parent,false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OffersHomeAdapter.ViewHolder holder, int position) {
        holder.offer_image.setImageResource(offers[position].getOffer_image());
    }

    @Override
    public int getItemCount() {
        return  offers.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView offer_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            offer_image=itemView.findViewById(R.id.offer_image);
        }
    }
}
