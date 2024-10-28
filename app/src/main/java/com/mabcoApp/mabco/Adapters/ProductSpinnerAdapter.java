package com.mabcoApp.mabco.Adapters;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mabcoApp.mabco.Classes.CategoryModel;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.R;

import java.util.List;

public class ProductSpinnerAdapter extends ArrayAdapter<Object> {
    private LayoutInflater layoutInflater;
    private Context context;
    private List<?> items;
    private boolean isProduct;
    private boolean isArrowDown = true;

    // Single constructor
    public ProductSpinnerAdapter(Context context, List<?> items, boolean isProduct) {
        super(context, 0, (List<Object>) items);
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.isProduct = isProduct;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.spinner_item_product, parent, false);
        } else {
            view = convertView;
        }

        Object item = getItem(position);
        if (isProduct && item instanceof Product) {
            Product product = (Product) item;
            ImageView ivProduct = view.findViewById(R.id.ivProduct);
            TextView tvProduct = view.findViewById(R.id.tvProduct);
            Glide.with(context).load(product.getProduct_image()).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(ivProduct);
            tvProduct.setText(product.getProduct_title());
        } else if (!isProduct && item instanceof CategoryModel) {
            CategoryModel category = (CategoryModel) item;
            ImageView ivProduct = view.findViewById(R.id.ivProduct);
            TextView tvProduct = view.findViewById(R.id.tvProduct);
            ivProduct.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            ivProduct.setImageResource(category.getImage());
            tvProduct.setText(category.getTitle());
        }
        else {
            TextView tvProduct = view.findViewById(R.id.tvProduct);
            tvProduct.setText(R.string.choose);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        try {
            if (position == 0) {
                view = layoutInflater.inflate(R.layout.header_product, parent, false);
                TextView tvProduct = view.findViewById(R.id.tvProduct);
                tvProduct.setText(R.string.choose);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        View root = parent.getRootView();
                        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                        root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                        ImageView arrow_down = root.findViewById(R.id.arrow_down);
                        // Toggle the arrow direction
                        if (isArrowDown) {
                            arrow_down.setImageResource(R.drawable.arrow_up);
                        } else {
                            arrow_down.setImageResource(R.drawable.arrow_down);
                        }
                        isArrowDown = !isArrowDown; // Toggle the state
                    }
                });
            } else {
                if (convertView == null) {
                    view = layoutInflater.inflate(R.layout.spinner_item_product, parent, false);
                } else {
                    view = convertView;
                }

                Object item = getItem(position); // Adjusted for header
                if (isProduct && item instanceof Product) {
                    Product product = (Product) item;
                    ImageView ivProduct = view.findViewById(R.id.ivProduct);
                    ImageView arrow_down = view.findViewById(R.id.arrow_down);
                    TextView tvProduct = view.findViewById(R.id.tvProduct);
                    Glide.with(context).load(product.getProduct_image()).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(ivProduct);
                    tvProduct.setText(product.getProduct_title());
                    if (position > 0) arrow_down.setVisibility(View.GONE);
                } else if (!isProduct && item instanceof CategoryModel) {
                    CategoryModel category = (CategoryModel) item;
                    ImageView ivProduct = view.findViewById(R.id.ivProduct);
                    TextView tvProduct = view.findViewById(R.id.tvProduct);
                    ImageView arrow_down = view.findViewById(R.id.arrow_down);
                    ivProduct.setColorFilter(ContextCompat.getColor(context, R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                    ivProduct.setImageResource(category.getImage());
                    tvProduct.setText(category.getTitle());
                    if (position > 0) arrow_down.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return null;
        }
        return items.get(position - 1);
    }

    @Override
    public int getCount() {
        return items.size() + 1;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }
}
