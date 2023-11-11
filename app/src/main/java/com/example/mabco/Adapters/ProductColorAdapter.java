package com.example.mabco.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.ProductColor;
import com.example.mabco.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ProductColorAdapter extends RecyclerView.Adapter<ProductColorAdapter.ViewHolder> {
    private Context context;
    private OnClickListener onClickListener;
    private ArrayList<ProductColor> productColors;
    public ProductColor selectedColor;
    private int selectedItem;
    private int selectedPos = RecyclerView.NO_POSITION;

    public void setOnClickListener(ProductColorAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ProductColorAdapter(Context context, ArrayList<ProductColor> productColors) {
        this.context = context;
        this.productColors = productColors;
    }

    public ProductColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(ProductColor selectedColor) {
        this.selectedColor = selectedColor;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    @NonNull
    @Override
    public ProductColorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ProductColorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductColorAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            ProductColor productColor = productColors.get(position);
            holder.itemView.setSelected(selectedPos == position);

            if (selectedItem == position) {
                holder.colorcardView.setStrokeColor(Color.parseColor(productColor.getColor_code()));
            } else
                holder.colorcardView.setStrokeColor(Color.parseColor("#00FFFFFF"));

            holder.colorimage.setBackgroundColor(Color.parseColor(productColor.getColor_code()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int previousItem = selectedItem;
                        selectedItem = position;
                        onClickListener.onClick(position, productColor);
                        notifyItemChanged(previousItem);
                        notifyItemChanged(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productColors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView colorcardView;
        ImageView colorimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorcardView = itemView.findViewById(R.id.colorCard);
            colorimage = itemView.findViewById(R.id.colorImage);
        }
    }

    public interface OnClickListener {
        void onClick(int position, ProductColor productColor);
    }
}
