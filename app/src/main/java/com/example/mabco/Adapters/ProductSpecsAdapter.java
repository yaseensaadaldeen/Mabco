package com.example.mabco.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.ProductSpecs;
import com.example.mabco.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductSpecsAdapter extends RecyclerView.Adapter<ProductSpecsAdapter.ViewHolder> {
    ArrayList<ProductSpecs> productSpecs;
    Context context;

    public ProductSpecsAdapter(ArrayList<ProductSpecs> productSpecs, Context context) {
        this.productSpecs = productSpecs;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductSpecsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.spec_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecsAdapter.ViewHolder holder, int position) {
        try {
            ProductSpecs productSpec = productSpecs.get(position);
            holder.spec_det.setText(productSpec.getSpec_desc());
            holder.spec_hdr.setText(productSpec.getSpec_title());
            if (!productSpec.getSpec_image().equals(""))
                Picasso.get().load(productSpec.getSpec_image()).fit().centerInside().into(holder.spec_image);
            else
                holder.spec_image.setBackgroundColor(Color.WHITE);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productSpecs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView spec_image;
        TextView spec_hdr, spec_det;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spec_image = itemView.findViewById(R.id.spec_image);
            spec_hdr = itemView.findViewById(R.id.spec_hdr);
            spec_det = itemView.findViewById(R.id.spec_det);
        }
    }
}
