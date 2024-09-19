package com.mabcoApp.mabco.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.Classes.ProductColor;
import com.mabcoApp.mabco.Classes.ShoppingCart;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.ui.ShoppingCart.ShoppingCartFragment;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartItemsAdapter extends RecyclerView.Adapter<ShoppingCartItemsAdapter.ViewHolder> {
    private Context context;
    private List<Product> products;
    private int items_count;
    private MainActivity mainActivity;
    View view;

    public ShoppingCartItemsAdapter(List<Product> products, Context context, MainActivity mainActivity , View view) {
        this.context = context;
        this.products = products;
        this.mainActivity = mainActivity;
        this.view = view;
    }

    @NonNull
    @Override
    public ShoppingCartItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shoppingcart_product_item, parent, false);
        return new ShoppingCartItemsAdapter.ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ShoppingCartItemsAdapter.ViewHolder holder, int position) {
        try {
            Product product = products.get(position);
            items_count = ShoppingCart.getProductItemsCount(context, product);
            holder.items_count.setText(String.valueOf(items_count));
            Glide.with(context).load(product.getProductColor().getImage_Link()).fitCenter().into(holder.product_image);
            holder.product_name.setText(product.getProduct_title());
            holder.product_price.setText((product.getShelf_price().contains(",") ? product.getShelf_price() : Long.parseLong(product.getShelf_price())) + " SP");

            if (!product.getDiscount().equals("0") && !product.getDiscount().equals(".00") && !product.getDiscount().equals("")) {
                holder.product_disc.setVisibility(View.VISIBLE);

                if (product.getCoupon().equals("0")) {
                    holder.product_disc.setText(String.valueOf((Long.parseLong(product.getShelf_price()))) + " SP");
                    String final_price = String.valueOf((Long.parseLong(product.getShelf_price()) - (Long.parseLong(product.getDiscount().replace(".00","")))));
                    holder.product_price.setText(final_price + " SP");
                    holder.product_disc.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.product_disc.setTextColor(Color.RED);
                } else {
                    holder.product_disc.setPaintFlags(holder.product_price.getPaintFlags());
                    holder.product_disc.setTextColor(Color.parseColor("#af0491cf"));
                    holder.product_disc.setText(" قسيمة شرائية " + Long.parseLong(product.getDiscount()) + " ل.س ");
                }
            }
            if (product.getCategoryModel().getCat_code().equals("00")) {
                holder.product_count.setVisibility(View.GONE);

            }

            ArrayList<ProductColor> productColors = new ArrayList<ProductColor>();
            productColors.add(product.getProductColor());
            ProductColorAdapter productColorAdapter = new ProductColorAdapter(context, productColors);
            holder.product_colors.setAdapter(productColorAdapter);
            productColorAdapter.notifyDataSetChanged();
            holder.increaseButton.setOnClickListener(v -> {
                ShoppingCart.addToCart(context, product);
                items_count = ShoppingCart.getProductItemsCount(context, product);
                holder.items_count.setText(String.valueOf(items_count));
                updateTextInMainActivity();
                updateprices();
            });
            updateprices();
            holder.decreaseButton.setOnClickListener(v -> {
                items_count = ShoppingCart.getProductItemsCount(context, product);
                if (items_count > 1) {
                    ShoppingCart.removeFromCart(context, product);
                    items_count = ShoppingCart.getProductItemsCount(context, product);
                    holder.items_count.setText(String.valueOf(items_count));
                    updateprices();

                    updateTextInMainActivity();
                } else {
                    Toast.makeText(context, "لم يتبقى كمية لطفا الإزالة في حال عدم الرغبة بالشراء", Toast.LENGTH_SHORT).show();
                }
            });
            holder.btn_remove.setOnClickListener(v -> {
                ShoppingCart.removeAllFromCart(context, product);
                removeItem(position);
                updateprices();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTextInMainActivity() {
        int shoppingcartItems = ShoppingCart.getItemCount(context);
        mainActivity.updateNotificationCount(shoppingcartItems);
    }

    public void removeItem(int position) {

        products.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        updateTextInMainActivity();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateprices() {
        ShoppingCartFragment shoppingCartFragment = new ShoppingCartFragment(context);
        shoppingCartFragment.UpdatePricesSet(ShoppingCart.GetTotalPrice(context), ShoppingCart.GetTotalDiscount(context), ShoppingCart.GetFinalPrice(context),view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btn_remove;
        Button increaseButton, decreaseButton;
        RecyclerView product_colors;
        ImageView product_image;
        TextView product_name, product_price, product_disc, items_count;
        LinearLayout product_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            product_colors = itemView.findViewById(R.id.product_colors);
            product_image = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_disc = itemView.findViewById(R.id.product_disc);
            items_count = itemView.findViewById(R.id.itemCountTextView);
            product_count = itemView.findViewById(R.id.product_count);
        }
    }
}
