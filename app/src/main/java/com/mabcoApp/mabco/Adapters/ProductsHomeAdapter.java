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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductsHomeAdapter extends RecyclerView.Adapter<ProductsHomeAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Product> products;
    public MainActivity mainActivity;
    String local;
    public static String lang;
    String fragment_name = "ProductsFragment";

    public ProductsHomeAdapter(Context context, ArrayList<Product> products, MainActivity mainActivity, String lang) {
        this.context = context;
        this.products = products;
        this.mainActivity = mainActivity;
        this.lang = lang;
        int productCount = Product.countComparedProducts(context);
        boolean notify = productCount > 0;

        //  if (mainActivity != null)
        //  mainActivity.AddToolbarNotification(productCount, R.id.nav_compare, notify);
    }

    public ProductsHomeAdapter(Context context, ArrayList<Product> products, MainActivity mainActivity, String lang, String fragment_name) {
        this.context = context;
        this.products = products;
        this.mainActivity = mainActivity;
        this.lang = lang;
        int productCount = Product.countComparedProducts(context);
        boolean notify = productCount > 0;
        this.fragment_name = fragment_name;

        //  if (mainActivity != null)
        //  mainActivity.AddToolbarNotification(productCount, R.id.nav_compare, notify);
    }

    @NonNull
    @Override
    public ProductsHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item_home, parent, false);

        return new ViewHolder(view);
    }

    private ProductsHomeAdapter.OnClickListener onClickListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int selectedItem;
    public ProductsHomeAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ProductsHomeAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            Activity activity = unwrap(context);
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            Product product = products.get(position);
            if (!fragment_name.equals("ProductsFragment")) {
                // Get the screen width

                int screenWidth = displaymetrics.widthPixels;

                // Define the number of columns (span count)
                int spanCount = context.getResources().getInteger(R.integer.grid_column_count);

                // Calculate the item width based on the screen width and span count
                float itemWidthPercentage = 0.5f / spanCount; // Calculate percentage based on span count
                int itemWidth = (int) (screenWidth * itemWidthPercentage); // Calculate item width

                // Set the item width dynamically
                ViewGroup.LayoutParams layoutParams = holder.product_card.getLayoutParams();
                layoutParams.width = itemWidth;
                holder.product_card.setLayoutParams(layoutParams);
            }
            if (!product.getStk_code().isEmpty()) {
                holder.product = product;
                Glide.with(context).load(product.getProduct_image()).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(holder.product_image);
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
                } else if (!product.getDiscount().equals("0") && !product.getDiscount().equals(".00") && !product.getDiscount().equals("")&& !product.getTag().equals("")) {
                    holder.product_tag.setTextColor(Color.WHITE);
                    holder.product_tag.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#af0491cf")));
                } else
                    holder.product_tag.setVisibility(View.GONE);


                if (!product.getDiscount().equals("0") && !product.getDiscount().equals(".00") && !product.getDiscount().equals("")) {
                    holder.product_disc.setVisibility(View.VISIBLE);

                    if (product.getCoupon().equals("0")) {
                        holder.product_disc.setText(formatPrice(product.getShelf_price()));
                        String final_price = formatPrice(String.valueOf((Long.parseLong(product.getShelf_price().replace(",", "")) - (Long.parseLong(product.getDiscount())))));
                        holder.product_price.setText(final_price);
                        holder.product_disc.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.product_disc.setTextColor(Color.RED);
                    } else {
                        holder.product_disc.setPaintFlags(holder.product_price.getPaintFlags());
                        holder.product_disc.setTextColor(Color.parseColor("#af0491cf"));
                        holder.product_disc.setText(lang.equals("ar") ? "  قسيمة شرائية  " +formatPrice( product.getDiscount()): "With Coupon " +formatPrice( product.getDiscount()));

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
                if (Product.isProductInComparison(context, product))
                    holder.compare_Card.setVisibility(View.VISIBLE);
                else holder.compare_Card.setVisibility(View.GONE);


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatPrice(String priceString) {
        try {
            // Parse the string to a long

            long priceValue = Long.parseLong(priceString.replace(",", "").replace(".00", ""));

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

    @Override
    public int getItemCount() {
        if (products != null) return products.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnLongClickListener {
        TextView product_name, product_desc, product_price, product_disc, product_tag;
        ImageView product_image;
        CardView compare_Card, product_card;
        boolean isLongPressed = false;
        Product product;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_desc = itemView.findViewById(R.id.product_desc);
            product_price = itemView.findViewById(R.id.product_price);
            product_disc = itemView.findViewById(R.id.product_disc);
            product_tag = itemView.findViewById(R.id.product_tag);
            compare_Card = itemView.findViewById(R.id.compare_Card);
            product_card = itemView.findViewById(R.id.product_card);
            itemView.setOnLongClickListener(this);
            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isLongPressed) {
                        scaleView(view, 1.0f, 1.1f); // Scale up
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isLongPressed) {
                        scaleView(view, 1.1f, 1.0f); // Scale back down
                        CompareAction(compare_Card,product);
                        isLongPressed = false;
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean onLongClick(View view) {
            isLongPressed = true;
            scaleView(view, 1.0f, 1.1f); // Scale up
            return true;
        }
    }


    private void scaleView(View view, float startScale, float endScale) {
        view.animate().scaleX(endScale).scaleY(endScale).setDuration(300).start();
    }

    public interface OnClickListener {
        void onClick(int position, Product product);
    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        return (Activity) context;
    }

    private void CompareAction(View view,Product product) {
        if (view.getVisibility() == View.VISIBLE) {
            if (Product.RemoveProductFromComparison(context, product))
                view.setVisibility(View.GONE);
        } else {
            if (Product.AddProductToComparison(context, product)) view.setVisibility(View.VISIBLE);
        }
        int productCount = Product.countComparedProducts(context);
        boolean notify = productCount > 0;
        if (mainActivity instanceof MainActivity)
            mainActivity.AddToolbarNotification(productCount, R.id.nav_compare, notify);
    }


}
