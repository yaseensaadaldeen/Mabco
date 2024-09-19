package com.mabcoApp.mabco.ui.Product;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mabcoApp.mabco.Adapters.FAQ_Adapter;
import com.mabcoApp.mabco.Classes.FAQ;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A fragment representing a list of Items.
 */
public class FAQFragment extends Fragment {
    View view;
    SharedPreferences preferences,PersonalPreference;
    String local;
    public Context context;
    private RecyclerView recyclerView;
    private List<FAQ> mList;
    private FAQ_Adapter adapter;
    Product product;
    public RequestQueue requestQueue;
    String brand_code, cat_code;

    public FAQFragment() {
    }

    public FAQFragment(Product product) {
        this.product = product;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_faq, container, false);
        context = view.getContext();
        assert context != null;
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");
        preferences = context.getSharedPreferences("HomeData", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.main_recyclervie);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FAQAPI(context, haveNetworkConnection());

        return view;
    }

    public void FAQAPI(Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            brand_code = "12";
            cat_code = product.getCategoryModel().getCat_code();

            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/getFAQs/"+local+"," + brand_code + "," + cat_code + "";
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("FAQs" + cat_code + brand_code, response);
                        editor.apply();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("GetFAQsResult");
                        if (array != null) {
                            LoadFAQ(array);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    FAQAPI(context,false);
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
            String FAQ = preferences.getString("FAQs" + cat_code + brand_code, "");
            if (!FAQ.equals("")) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(FAQ);
                    JSONArray array = jsonResponse.optJSONArray("GetFAQsResult");
                    if (array != null) {
                        LoadFAQ(array);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void LoadFAQ(JSONArray array) {

        mList = new ArrayList<>();
        ArrayList<String> question = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject arrayObj = array.getJSONObject(i);
                List<String> nestedList1 = new ArrayList<>();
                nestedList1.add(arrayObj.optString("Answer"));
                question.add( arrayObj.optString("Question"));
                mList.add(new FAQ(nestedList1, arrayObj.optString("Question")));

            }
            adapter = new FAQ_Adapter(mList,question);
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE.toString());
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}