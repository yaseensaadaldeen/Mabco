package com.example.mabco.ui.Showrooms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mabco.Adapters.ShowroomAdapter;
import com.example.mabco.Classes.NetworkStatus;
import com.example.mabco.Classes.Showroom;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowroomsFragment extends Fragment {
    Context context;
    View view;
    RecyclerView showroomitems;
    ShowroomAdapter showroomAdapter;
    SharedPreferences showroomsPreferance;
    public RequestQueue requestQueue;
    ShimmerFrameLayout showroomshimmerViewContainer;
    String local = "en";
    SharedPreferences PersonalPreference;
    ArrayList<Showroom> showrooms;


    public static ShowroomsFragment newInstance() {
        return new ShowroomsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_showrooms, container, false);
        context = getContext();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE)
            showNavigationBar();
        showroomsPreferance = context.getSharedPreferences("ShowroomData", Context.MODE_PRIVATE);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");
        showroomitems = view.findViewById(R.id.showrooms_recycle);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        showroomitems.setLayoutManager(layoutManager);
        showroomshimmerViewContainer = view.findViewById(R.id.showroomshimmerViewContainer);
        showrooms = new ArrayList<>();
        showroomAdapter = new ShowroomAdapter(context, showrooms);
        showroomitems.setAdapter(showroomAdapter);
        showroomAdapter.setOnClickListener(new ShowroomAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Showroom showroom) {
                ShowroomDetails showroomDetails = new ShowroomDetails(context, showroom);
                showroomDetails.show(getActivity().getSupportFragmentManager(), "TAG");
            }
        });
        ShowroomsAPI(context, NetworkStatus.isOnline(context));
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void ShowroomsAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.example.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/getshowroomssites/"+local;
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = showroomsPreferance.edit();
                        editor.putString("Showrooms", response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("GetShowroomsSitesResult");
                        if (array != null) {
                            LoadShowrooms(array);
                            showroomshimmerViewContainer.stopShimmer();
                            showroomshimmerViewContainer.setVisibility(View.GONE);
                            showroomAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
            };
            try {
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                );
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String NewArrivals = showroomsPreferance.getString("Showrooms", "");
                if (!NewArrivals.equals("")) {
                    JSONObject jsonResponse = new JSONObject(NewArrivals);
                    JSONArray array = jsonResponse.optJSONArray("GetShowroomsSitesResult");
                    if (array != null) {
                        LoadShowrooms(array);
                        showroomshimmerViewContainer.stopShimmer();
                        showroomshimmerViewContainer.setVisibility(View.GONE);
                        showroomAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    void LoadShowrooms(JSONArray array) throws JSONException {
        try {
            showrooms.clear();
            try {
                for (int i = 0; i < array.length(); i++) {

                    JSONObject arrayObj = array.getJSONObject(i);
                    Showroom showroom = new Showroom(arrayObj.optString("Loc_code"),
                            arrayObj.optString("Loc_name"),
                            "https://" + arrayObj.optString("Image_Link"),
                            arrayObj.optString("Phone"),
                            arrayObj.optString("City_name"),
                            arrayObj.optString("Address"),
                            arrayObj.optString("Winter_from_date"),
                            arrayObj.optString("Winter_to_date"),
                            arrayObj.optString("week_end")
                    );

                    showrooms.add(showroom);
                }
                showroomitems.setAdapter(showroomAdapter);
                showroomAdapter.notifyDataSetChanged();
                showroomshimmerViewContainer.stopShimmer();
                showroomshimmerViewContainer.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}