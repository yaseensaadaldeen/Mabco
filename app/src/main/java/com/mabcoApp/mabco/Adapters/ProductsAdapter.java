package com.mabcoApp.mabco.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>{
    private Context context;
    public ArrayList<Product> products;

    public ProductsAdapter(Context context, ArrayList<Product> products,String lang) {
        this.context = context;
        this.products = products;
        this.lang = lang;
    }
    private ProductsAdapter.OnClickListener onClickListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int selectedItem;
    static String lang;

    public ProductsAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ProductsAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item,parent,false);
        return new ProductsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Activity activity = unwrap(context);
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Product product = products.get(position);
        if (!product.getStk_code().isEmpty()) {
            Glide.with(context).load(product.getProduct_image()).fitCenter().into(holder.product_image);
            holder.product_name.setText(product.getProduct_title());
            holder.product_desc.setText(product.getStk_desc());
            holder.product_price.setText(formatPrice(product.getShelf_price()));
            holder.product_tag.setText(product.getTag());
            if (product.getTag().equals("new")) {
                holder.product_tag.setTextColor(Color.GRAY);
                holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
            } else if (product.getTag().equals("best")) {
                holder.product_tag.setTextColor(Color.WHITE);
                holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
            } else if (!product.getDiscount().equals("0") && !product.getDiscount().equals(".00") && !product.getDiscount().equals("")) {
                holder.product_tag.setTextColor(Color.WHITE);
                holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#af0491cf")));
            } else holder.product_tag.setVisibility(View.GONE);


            if (!product.getDiscount().equals("0") && !product.getDiscount().equals(".00") && !product.getDiscount().equals("")) {
                holder.product_disc.setVisibility(View.VISIBLE);

                if (product.getCoupon().equals("0")) {
                    holder.product_disc.setText(formatPrice(product.getShelf_price()));
                    String final_price =  formatPrice(String.valueOf((Long.parseLong(product.getShelf_price().replace(",","")) - (Long.parseLong(product.getDiscount())))));
                    holder.product_price.setText(final_price );
                    holder.product_disc.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.product_disc.setTextColor(Color.RED);
                } else {
                    holder.product_disc.setPaintFlags(holder.product_price.getPaintFlags());
                    holder.product_disc.setTextColor(Color.parseColor("#af0491cf"));
                    holder.product_disc.setText(lang.equals("ar") ? "  قسيمة شرائية"+formatPrice( product.getDiscount()) : "With Coupon " +formatPrice( product.getDiscount()));

                }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null) {

                        onClickListener.onClick(position, product);
                    }
                }
            });
        }
    }
    public static String formatPrice(String priceString ) {
        try {
            // Parse the string to a long

            long priceValue = Long.parseLong(priceString.replace(",","").replace(".00",""));

            // Get a NumberFormat instance for formatting with US locale (uses commas)
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

            // Format the long value
            return numberFormat.format(priceValue) + (lang.equals("ar") ? " ل.س " : " SP");
        } catch (NumberFormatException e) {
            // Handle the exception if parsing fails
            e.printStackTrace();
            return "Invalid Price";
        }
    }

    public interface OnClickListener {
        void onClick(int position, Product product);
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

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
}
