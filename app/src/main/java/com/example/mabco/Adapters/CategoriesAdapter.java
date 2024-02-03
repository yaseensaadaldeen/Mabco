package com.example.mabco.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.CategoryModel;
 import com.example.mabco.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private Context context;
    public CategoryModel[] categoryModels;
    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(CategoriesAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public CategoriesAdapter(Context context , CategoryModel[] categoryModels) {

        this.context = context;
        this.categoryModels = categoryModels;

    }


    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item,parent,false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);


        holder.category_title.setText(categoryModels[position].getTitle());
        holder.category_image.setImageResource(categoryModels[position].getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {

                    onClickListener.onClick(position, categoryModels[position]);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoryModels.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView catcard;
        CardView catimg;

        TextView category_title ;
        ImageView category_image ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catcard= itemView.findViewById(R.id.cat);
            catimg= itemView.findViewById(R.id.image_card);

            category_title = itemView.findViewById(R.id.category_title);
            category_image = itemView.findViewById(R.id.category_image);

        }
      
    }
    public interface OnClickListener {
        void onClick(int position, CategoryModel categoryModel);
    }
}
