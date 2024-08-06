package com.mabcoApp.mabco.ui.Offers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mabcoApp.mabco.Adapters.OfferAdapter;
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.ui.Product.OfferProductDialog;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OffersFragment extends Fragment {
    private RequestQueue requestQueue;
    public Context context;
    public ArrayList<Offer> Offers;
    SharedPreferences preferences, PersonalPreference;

    RecyclerView offerRecyclerView;
    OfferAdapter offerAdapter;
    String lang;
    View view;
    ShimmerFrameLayout OffershimmerViewContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offers, container, false);
        context = getContext();
        assert context != null;
        preferences = context.getSharedPreferences("OffersData", Context.MODE_PRIVATE);
        OffershimmerViewContainer = view.findViewById(R.id.offer_shimmer_view_container);
        OffershimmerViewContainer.startShimmer();
        offerRecyclerView = view.findViewById(R.id.Offers_slider);
        int orientation = getResources().getConfiguration().orientation;
        int spanCount;
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE)
            showNavigationBar();
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        lang = PersonalPreference.getString("Language", "ar");
        offerRecyclerView.setAdapter(offerAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            spanCount = 2;

        } else {
            // code for landscape mode
            spanCount = 4;
        }
        gridLayoutManager.setSpanCount(spanCount);
        offerRecyclerView.setLayoutManager(gridLayoutManager);

        OffersAPI(context, NetworkStatus.isOnline(context));
        return view;
    }
    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
    public void openDialog(Offer offer) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        OfferProductDialog listDialog = new OfferProductDialog(context, offer,navController,"offerFragment") {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        };
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(listDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1700;
        listDialog.show();
        listDialog.getWindow().setAttributes(lp);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void OffersAPI(Context context, boolean online) {
        requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/getoffers/" + lang;
        if (online) {
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("OffersDetails", response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray offersArray = jsonResponse.optJSONArray("GetOffersResult");
                        LoadOffers(offersArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            }) {
            };
            try {
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String Offers = preferences.getString("OffersDetails", "");
            if (!Offers.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(Offers);
                    JSONArray offersArray = jsonResponse.optJSONArray("getStockOffers");
                    LoadOffers(offersArray);
                    OffershimmerViewContainer.stopShimmer();
                    OffershimmerViewContainer.setVisibility(View.GONE);
                    offerAdapter.notifyDataSetChanged();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadOffers(JSONArray offersArray) throws JSONException {
        try {

            Offers = new ArrayList<>();
            JSONObject offersOBJ = offersArray.getJSONObject(0);
            Offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), "mabcoonline.com/images/soon.png"));
            Offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), "mabcoonline.com/images/discount.png"));
            Offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), "mabcoonline.com/images/discountpersent.png"));

            for (int j = 0; j < offersArray.length(); j++) {
                 offersOBJ = offersArray.getJSONObject(j);
                Offers.add(new Offer(offersOBJ.optString("Offer_no"), offersOBJ.optString("Device_title"), offersOBJ.optString("Device_title"), offersOBJ.optString("Image_link")));
            }

            offerAdapter = new OfferAdapter(context, Offers);
            offerRecyclerView.setAdapter(offerAdapter);
            offerAdapter.setOnClickListener(new OfferAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Offer offer) {
                    openDialog(offer);
                }
            });
            OffershimmerViewContainer.stopShimmer();
            OffershimmerViewContainer.setVisibility(View.GONE);
            offerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}