package com.mabcoApp.mabco.Classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.mabcoApp.mabco.R;

import java.util.ArrayList;


/**
 * Created by Rafat Haroub on 14/11/2015.
 */
public class NotificationAdapter extends BaseAdapter{
    Context context;
    ArrayList<Notification> notifications;
    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {

        this.notifications = notifications;

        this.context = context;
    }

    @Override
    public int getCount(){
        return notifications.size();
    }


    @Override
    public Object getItem(int position){
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position){
        return   notifications.size();     //Long.valueOf(offer.get(position).getOffer_no());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null)
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.recycler_notification_item, null);
        TextView name = (TextView) convertView.findViewById(R.id.titel);
        TextView des = (TextView) convertView.findViewById(R.id.detels);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        Typeface type = ResourcesCompat.getFont(context, R.font.rafat);
        name.setTypeface(type);
        des.setTypeface(type);

//        name.setTextColor(Color.parseColor("#000000"));
        des.setTextColor(Color.parseColor("#000000"));

        name.setText(notifications.get(position).getNotificationTitle());
        des.setText(notifications.get(position).getNotificationText());
        String temp = notifications.get(position).getImageLink();
        final String link = temp.replaceAll(" ", "%20");
        /*Picasso*/
        Glide.with(context)
                .load("https://"+ link)
//                .thumbnail(Glide.with(context).load(R.drawable.progress_animation))
                .apply(new RequestOptions()
                        .override(500,500)).transition(new DrawableTransitionOptions()
                        .crossFade())
                .into(image);

        return convertView;
    }
}
