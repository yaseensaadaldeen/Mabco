package com.example.mabco.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.Brand;
import com.example.mabco.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Brand> brands;
    private OnClickListener onClickListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int selectedItem;
    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(BrandAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public BrandAdapter(Context context, ArrayList<Brand> brands) {
        this.context = context;
        this.brands = brands;
    }

    @NonNull
    @Override
    public BrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.brand_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Brand brand = brands.get(position);
        Picasso.get().load(brand.getBrand_image()).into(holder.brandImageView);
        holder.itemView.setSelected(selectedPos == position);
        if (selectedItem == position) {
            holder.brandImageView.setBackgroundColor(context.getResources().getColor(R.color.no1));
        } else
            holder.brandImageView.setBackgroundColor(context.getResources().getColor(R.color.no2));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int previousItem = selectedItem;
                selectedItem = position;
                onClickListener.onClick(position, brand);
                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView brandImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandImageView = itemView.findViewById(R.id.brand_image);
        }

    }
    public interface OnClickListener {
        void onClick(int position, Brand brand);
    }
}
