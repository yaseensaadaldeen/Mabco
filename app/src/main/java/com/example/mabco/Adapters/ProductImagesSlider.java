package com.example.mabco.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.smarteist.autoimageslider.SliderViewAdapter;

import org.json.JSONArray;

import java.util.ArrayList;

public class ProductImagesSlider extends SliderViewAdapter<ProductImagesSlider.SliderAdapterVH> {
    private Context context;
    private JSONArray mSliderItems = null;
    private ArrayList<String> ImagesURL ;
    private ProductColorAdapter.OnClickListener onClickListener;
    private boolean zoomOut = false;

    public ProductImagesSlider(Context context, ArrayList<String> imagesURL) {
        this.context = context;
        ImagesURL = imagesURL;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

    }

    @Override
    public int getCount() {
        return ImagesURL.size();
    }

    public class SliderAdapterVH extends ViewHolder {
        public SliderAdapterVH(View itemView) {
            super(itemView);
        }
    }
}
