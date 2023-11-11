package com.example.mabco.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.Brand;
import com.example.mabco.Classes.Product;
import com.example.mabco.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>{
    private Context context;
    public ArrayList<Product> products;

    public ProductsAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }
    private ProductsAdapter.OnClickListener onClickListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int selectedItem;
    public ProductsAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ProductsAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_home,parent,false);
        return new ProductsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        Picasso.get().load(product.getProduct_image()).fit().centerInside().into(holder.product_image);
        holder.product_name.setText(product.getProduct_title());
        holder.product_desc.setText(product.getStk_desc());
        holder.product_price.setText( (product.getShelf_price().contains(",")?product.getShelf_price() :Long.parseLong(product.getShelf_price()) )+" SP");
        holder.product_tag.setText(product.getTag());
        if (product.getTag().equals("new")  ) {
            holder.product_tag.setTextColor(Color.GRAY);
            holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        }
        else if (product.getTag().equals("best") ) {
            holder.product_tag.setTextColor(Color.WHITE);
            holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        } else if (!product.getDiscount().equals("0")) {
            holder.product_tag.setTextColor(Color.WHITE);
            holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#af0491cf")));
        }
        else   holder.product_tag.setVisibility(View.GONE);

        if ( !product.getDiscount().equals("0"))
        {
            holder.product_disc.setVisibility(View.VISIBLE);

            if (product.getCoupon().equals("0"))
            {
                holder.product_disc.setText(String.valueOf((Long.parseLong(product.getShelf_price())))+" SP");
                String final_price = String.valueOf((Long.parseLong(product.getShelf_price())-(Long.parseLong(product.getDiscount()))));
                holder.product_price.setText(final_price+" SP");
                holder.product_disc.setPaintFlags( Paint.STRIKE_THRU_TEXT_FLAG);
                holder.product_disc.setTextColor(Color.RED);
            }
            else
            {
                holder.product_disc.setPaintFlags( holder.product_price.getPaintFlags());
                holder.product_disc.setTextColor(Color.parseColor("#af0491cf"));
                holder.product_disc.setText(" قسيمة شرائية " + Long.parseLong(product.getDiscount())+" ل.س ");
            }
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product_name, product_desc,product_price,product_disc,product_tag;
        ImageView product_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_desc = itemView.findViewById(R.id.product_desc);
            product_price = itemView.findViewById(R.id.product_price);
            product_disc = itemView.findViewById(R.id.product_disc);
            product_tag = itemView.findViewById(R.id.product_tag);
        }
    }
    public interface OnClickListener {
        void onClick(int position, Brand brand);
    }
}
