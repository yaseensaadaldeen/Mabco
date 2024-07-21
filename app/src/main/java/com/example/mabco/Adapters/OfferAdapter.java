package com.example.mabco.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mabco.Classes.Offer;
import com.example.mabco.R;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    private final Context context;
    public ArrayList<Offer> offers;
    private OfferAdapter.OnClickListener onClickListener;
    String from;

    public void setOnClickListener(OfferAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OfferAdapter(Context context, ArrayList<Offer> offers) {
        this.context = context;
        this.offers = offers;
    }

    public OfferAdapter(Context context, ArrayList<Offer> offers, String from) {
        this.context = context;
        this.offers = offers;
        this.from = from;
    }

    @NonNull
    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (from.equals("Home"))
            view = LayoutInflater.from(context).inflate(R.layout.offer_home_item, viewGroup, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.offers_fragment_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        try {
            if (offers != null) {
                Glide.with(context)
                        .load("https://" + offers.get(i).getOffer_image_url())
                        .fitCenter().into(viewHolder.offer_image);
                //viewHolder.offer_image.setImageResource(offers.get(i).getOffer_image());
                viewHolder.offer_desc.setText(offers.get(i).getOffer_title());
                viewHolder.Offer = offers.get(i);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (onClickListener != null) {

                                onClickListener.onClick(i, viewHolder.Offer);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView offer_image;
        TextView offer_desc;
        public Offer Offer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            offer_image = itemView.findViewById(R.id.offer_image);
            offer_desc = itemView.findViewById(R.id.offer_desc);
        }
    }

    public interface OnClickListener {
        void onClick(int position, Offer offer);
    }
}
