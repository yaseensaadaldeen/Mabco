package com.example.mabco.Adapters;


import android.content.Context;
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

    public ShowroomAdapter() {
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

    @Override
    public void onBindViewHolder(@NonNull ShowroomAdapter.ViewHolder holder, int position) {
        try {
            Showroom showroom = showrooms.get(position);
            holder.showroom_name.setText( showroom.getLoc_name());
            holder.showroom_city.setText( showroom.getCity());
            holder.showroom_phon_no.setText( showroom.getPhone_no());
            holder.showroom_desc.setText( showroom.getLoc_desc());
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
}
