package com.mabcoApp.mabco.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mabcoApp.mabco.Classes.SavingList;
import com.mabcoApp.mabco.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SavingListAdapter extends SliderViewAdapter<SavingListAdapter.SavingListAdapterVH> {

    private Context context;
    private ArrayList<SavingList> savingLists;
    private OnClickListener onClickListener; // Interface for click listener

    // Constructor
    public SavingListAdapter(Context context, ArrayList<SavingList> savingLists) {
        this.context = context;
        this.savingLists = savingLists;
    }

    // Interface for click listener
    public interface OnClickListener {
        void onClick(int position, SavingList savingList);
    }

    // Method to set the click listener
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public SavingListAdapter.SavingListAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SavingListAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(final SavingListAdapterVH viewHolder, final int position) {
        SavingList savingList = savingLists.get(position);

        // Load image using Glide
        Glide.with(context).load(savingList.getImage_link()).fitCenter().into(viewHolder.imageViewBackground);

        // Set click listener on itemView
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, savingList);
                }
            }
        });
    }

    @Override
    public int getCount() {
        return savingLists != null ? savingLists.size() : 0;
    }

    // ViewHolder class
    static class SavingListAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;

        public SavingListAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
