package com.mabcoApp.mabco.ui.Offers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mabcoApp.mabco.Adapters.OfferAdapter;
import com.mabcoApp.mabco.Classes.GridSpacingItemDecoration;
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    GridLayoutManager gridLayoutManager;
    GridLayout offers_grid;
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
        if (getArguments() != null) {
            Offer offer = getArguments().getParcelable("Offer");

            // Use the product object to display product details
            if (offer == null) {
                Toast.makeText(getContext(), "Error: Product not found!", Toast.LENGTH_SHORT).show();
                // Navigate back
                requireActivity().onBackPressed();
            } else {
                openDialog(offer);
            }
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE) showNavigationBar();
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        lang = PersonalPreference.getString("Language", "ar");
        offerRecyclerView.setAdapter(offerAdapter);
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = getResources().getInteger(R.integer.offer_grid_column_count);
        gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        offers_grid = view.findViewById(R.id.Offers_grid);

        // Adding space between items
        int spacing = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        offerRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, false));

        gridLayoutManager.setSpanCount(spanCount);
        offerRecyclerView.setLayoutManager(gridLayoutManager);
        offers_grid.setColumnCount(spanCount);
        offers_grid.setLayoutDirection(spacing);
        if (savedInstanceState == null) OffersAPI(context, NetworkStatus.isOnline(context));
        else OffersAPI(context, false);

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
        SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String UserID = Token.getString("UserID", "");
        String url = UrlEndPoint.General + "Service1.svc/getoffers/" + lang + "," + UserID;
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
                    OffersAPI(context, false);
                }
            }) {

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