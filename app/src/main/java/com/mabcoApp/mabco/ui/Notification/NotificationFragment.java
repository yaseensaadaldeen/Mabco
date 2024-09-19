package com.mabcoApp.mabco.ui.Notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mabcoApp.mabco.Classes.Notification;
import com.mabcoApp.mabco.Classes.NotificationAdapter;
import com.mabcoApp.mabco.HttpsTrustManager;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment {
    RequestQueue requestQueue;
    ArrayList<Notification> notifications;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences sharedPreferences;
    private Context context;

    private GridView l1;
    private NotificationAdapter offeAdapter;
    private ProgressBar pbart;
    RequestQueue queue;
    String from;
    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment init(Context context, RequestQueue requestQueue) {
        NotificationFragment homeFragment = new NotificationFragment();
//        homeFragment.isOfferFragment = isOfferFragment;
        homeFragment.context = context;
        homeFragment.requestQueue = requestQueue;
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getContext();
        queue = Volley.newRequestQueue(context);
        requestQueue = Volley.newRequestQueue(context);
        Bundle b = getActivity().getIntent().getExtras();
        try {
            if (b != null && getArguments() != null) {
                from = getArguments().getString("from");
                sharedPreferences = context.getSharedPreferences("NotificationPref", 0);
                if (from != null) {
                    sharedPreferences.edit().putString("from", from).apply();
                } else {
                    sharedPreferences.edit().putString("from", null).apply();
                }
            }
        } catch (NullPointerException e) {
            sharedPreferences.edit().putString("from", null).apply();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = context.getSharedPreferences("NotificationPref", 0);
        pbart = (ProgressBar) view.findViewById(R.id.progressBar19);

        l1 = (GridView) view.findViewById(R.id.listView2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            l1.setNestedScrollingEnabled(true);
        }
        getRacesData();
        insertAPPLog("Notification Page");
    }

    public void getRacesData() {
        notifications = new ArrayList<>();

        offeAdapter = new NotificationAdapter(notifications, context);
        l1.setAdapter(offeAdapter);
        pbart.setVisibility(View.VISIBLE);

        l1.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                Bundle params = new Bundle();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                params.putString("type", notifications.get(position).getNotificationType());
                params.putString("stk_code", String.valueOf(notifications.get(position).getId()).toString().split("_")[0]);
                params.putString("date", date.toString());

                mFirebaseAnalytics.logEvent("NotificationPress", params);


                sharedPreferences = context.getSharedPreferences("NotificationPref", 0);
                String lang = "EN";
                if (sharedPreferences.getBoolean("ar", false)) {
                    lang = "AR";
                }
            }
        });
        try {
            HttpsTrustManager.allowAllSSL();
            StringRequest strRequest = new StringRequest(Request.Method.GET, UrlEndPoint.General + "service1.svc/getNotifications", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        pbart.setVisibility(View.GONE);
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray getNotificationsResult = jsonResponse.optJSONArray("getNotificationsResult");

                        inflateInView(getNotificationsResult);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    pbart.setVisibility(View.GONE);
                    Toast.makeText(context, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("X-Content-Type-Options", "nosniff");
                    params.put("X-XSS-Protection", "0");
                    params.put("X-Frame-Options", "DENY");
                    //..add other headers
                    return params;
                }
            };


            strRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(strRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void inflateInView(JSONArray array) {
        pbart.setVisibility(View.GONE);
        try {
            if (array == null) {
                Snackbar.make(l1, "connection error ", Snackbar.LENGTH_INDEFINITE).setAction("refresh", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getRacesData();
                    }
                }).show();
                return;
            }

            if (array.length() == 0) {
                Toast.makeText(context, getResources().getString(R.string.empty_notifications), Toast.LENGTH_LONG).show();
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject pp = array.getJSONObject(i);
                String notificationTitle = pp.optString("notification_title");
                String notificationText = pp.optString("notification_text").replace("$", "\n");
                String imageLink = pp.optString("image_link");
                String notificationType = pp.optString("notification_type");
                String notificationDate = pp.optString("notification_date");
                String notificationInfo = pp.optString("notification_info");
                String notificationType2 = pp.optString("notification_type2");

                // Create a new Notification object and add it to the list
                Notification notification = new Notification(i, // You can use a counter or any unique identifier for ID
                        notificationTitle, notificationText, imageLink, notificationType, notificationDate, notificationInfo, notificationType2);

                notifications.add(notification);
            }


            ((BaseAdapter) l1.getAdapter()).notifyDataSetChanged();
            pbart.setVisibility(View.GONE);
            offeAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void insertAPPLog(String destination) {
        SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String UserID = Token.getString("UserID", "");

        // Define the URL for your API endpoint
        String url = UrlEndPoint.General + "service1.svc/insertAPPLog/" + UserID + "," + destination;

        // Create a JSONObject to send in the request body

        // Create a Volley request
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Handle the successful response
                Log.d("Volley", "Notification acknowledgment sent: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Log.e("Volley", "Error sending acknowledgment: " + error.getMessage());
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-Content-Type-Options", "nosniff");
                params.put("X-XSS-Protection", "0");
                params.put("X-Frame-Options", "DENY");
                //..add other headers
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

}

