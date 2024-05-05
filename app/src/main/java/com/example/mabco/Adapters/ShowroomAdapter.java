package com.example.mabco.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mabco.Classes.Showroom;
import com.example.mabco.R;

import java.util.ArrayList;

public class ShowroomAdapter extends RecyclerView.Adapter<ShowroomAdapter.ViewHolder> {
    private Context context;
    public ArrayList<Showroom> showrooms;
    private ShowroomAdapter.OnClickListener onClickListener;
    private int selectedPos = RecyclerView.NO_POSITION;
    private int selectedItem;
    public ShowroomAdapter.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(ShowroomAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ShowroomAdapter(Context context, ArrayList<Showroom> showrooms) {
        this.context = context;
        this.showrooms = showrooms;
    }

    @NonNull
    @Override
    public ShowroomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.showroom_card,parent,false);
        return new ShowroomAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ShowroomAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            Showroom showroom = showrooms.get(position);
            holder.showroom_name.setText( showroom.getLoc_name());
            holder.showroom_city.setText( showroom.getCity());
            holder.showroom_phon_no.setText( showroom.getPhone_no());
            holder.showroom_desc.setText( showroom.getLoc_desc());
            holder.itemView .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ShowroomAdapter", "Item clicked at position: " + position);
                    if (onClickListener != null) {
                        onClickListener.onClick(position, showroom);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return showrooms != null ? showrooms.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView showroom_name ,showroom_desc , showroom_city,showroom_phon_no;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showroom_name = itemView.findViewById(R.id.showroom_name);
            showroom_desc = itemView.findViewById(R.id.showroom_desc);
            showroom_city = itemView.findViewById(R.id.showroom_city);
            showroom_phon_no = itemView.findViewById(R.id.showroom_phone_no);
        }
    }
    public interface OnClickListener {
        void onClick(int position, Showroom showroom);
    }
    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
}
