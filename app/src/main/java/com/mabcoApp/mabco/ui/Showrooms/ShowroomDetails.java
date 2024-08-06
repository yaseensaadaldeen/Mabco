package com.mabcoApp.mabco.ui.Showrooms;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mabcoApp.mabco.Classes.Showroom;
import com.mabcoApp.mabco.R;


public class ShowroomDetails extends BottomSheetDialogFragment {
    Context context ;
    Showroom showroom;

    ImageView showroom_image;
    TextView showroom_name , showroom_city , showroom_phone,shift_start , shift_end,week_end , address;

    public ShowroomDetails(Context context,Showroom showroom) {
        this.context=context;
        this.showroom = showroom;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.showroom_details,container,false);
        showroom_image=view.findViewById(R.id.showroom_image);
        showroom_name = view.findViewById(R.id.showroom_name);
        showroom_city = view.findViewById(R.id.showroom_city);
        showroom_phone = view.findViewById(R.id.phone);
        shift_start = view.findViewById(R.id.shoft_from);
        shift_end = view.findViewById(R.id.shift_to);
        week_end = view.findViewById(R.id.closing_days);
        address = view.findViewById(R.id.address);

        showroom_name.setText(showroom.getLoc_name());
        showroom_city.setText(showroom.getCity());
        showroom_phone.setText(showroom.getPhone_no());
        shift_start.setText(showroom.getShift_start());
        shift_end.setText(showroom.getShift_end());
        week_end.setText(showroom.getWeek_end());
        address.setText(showroom.getLoc_desc());
        Glide.with(context)
                .load( showroom.getImage_link())
                .into(showroom_image);
        return view;
    }
}
