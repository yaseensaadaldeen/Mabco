package com.mabcoApp.mabco.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabcoApp.mabco.Classes.Brand;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductsBrandAdapter extends RecyclerView.Adapter<ProductsBrandAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Brand> brands;
    public ProductsHomeAdapter brandproductsHomeAdapter;
    public MainActivity mainActivity;

    public ProductsBrandAdapter(Context context, ArrayList<Brand> brands) {
        this.context = context;
        this.brands = brands;
    }

    @NonNull
    @Override
    public ProductsBrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_brand_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsBrandAdapter.ViewHolder holder, int position) {
        try {
        Brand brand = brands.get(position);
        Picasso.get().load(brand.getBrand_image()).into(holder.brandImageView);
       // brandproductsHomeAdapter = new ProductsHomeAdapter(context, brands.get(position).getProducts(),((MainActivity) getActivity()));
        holder.productRecycle.setAdapter(brandproductsHomeAdapter);//.notifyDataSetChanged();
        holder.productRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        }
        catch (Exception ex)
        {
            Log.i("brandAdapter exception",ex.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView productRecycle ;
        ImageView brandImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productRecycle = itemView.findViewById(R.id.brand_products);
            brandImageView = itemView.findViewById(R.id.brand_full_image);
        }
    }
}
