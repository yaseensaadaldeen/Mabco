package com.mabcoApp.mabco.ui.Invoices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mabcoApp.mabco.Adapters.InvoicesAdapter;
import com.mabcoApp.mabco.Classes.Invoice_Det;
import com.mabcoApp.mabco.Classes.Invoice_Hdr;
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.HttpsTrustManager;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InvoicesFragment extends Fragment {
    RecyclerView InvoiceRecyclerView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    ArrayList<Invoice_Hdr> invoices_hdrs;
    SharedPreferences UserPreferance, PersonalPreference, InvoicesPreference;
    InvoicesAdapter invoicesAdapter;
    ShimmerFrameLayout InvoiceshimmerViewContainer;
    public RequestQueue requestQueue;
    Context context;
    NavController navController;
    View view;
    String local = "en";
    String phone_no = "";

    public InvoicesFragment newInstance() {
        return new InvoicesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_invoices, container, false);
        context = getContext();
        try {
            final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
            if (navBar != null && navBar.getVisibility() == View.INVISIBLE)
                showNavigationBar();

            PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
            InvoicesPreference = context.getSharedPreferences("InvoiceData", Context.MODE_PRIVATE);
            local = PersonalPreference.getString("Language", "ar");
            UserPreferance = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            InvoiceRecyclerView = view.findViewById(R.id.invoice_recycle);

            if (!UserPreferance.getBoolean("Verified", false) || UserPreferance.getString("PhoneNO", "").equals("")) {
                Toast.makeText(context, R.string.recomend_signin, Toast.LENGTH_LONG).show();
                navController.navigate(R.id.action_invoicesFragment_to_signInMain);
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            InvoiceRecyclerView.setLayoutManager(layoutManager);
            InvoiceshimmerViewContainer = view.findViewById(R.id.InvoiceshimmerViewContainer);
            invoices_hdrs = new ArrayList<>();
            invoicesAdapter = new InvoicesAdapter(context, invoices_hdrs);


            InvoiceRecyclerView.setAdapter(invoicesAdapter);

            invoicesAdapter.setOnClickListener(new InvoicesAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Invoice_Hdr invoice) {
                    Log.d("InvoicesFragment", "Invoice clicked: " + invoice.getInvoice_no());
                    openDialog(invoice.getInvoice_no());
                }
            });

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }

            phone_no = UserPreferance.getString("PhoneNO", "");
            getInvoicesOnline(phone_no, context, NetworkStatus.isOnline(context));
            insertAPPLog("Invoices Page");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }


    // Handle the permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with the file operations
                } else {
                    // Permission denied, handle the functionality accordingly
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getInvoicesOnline(final String phone, Context context, boolean online) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/getInvoiceHdr/" + phone;
            Log.d("InvoicesFragment", "Request URL: " + url); // Debug log

            StringRequest strRequest = new StringRequest(
                    Request.Method.GET,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                SharedPreferences.Editor editor = InvoicesPreference.edit();
                                editor.putString("Invoices", response);
                                editor.apply();
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray InvoicesHdr = jsonResponse.optJSONArray("GetInvoiceHdrResult");
                                if (InvoicesHdr == null) {
                                    Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                                } else {
                                    if (InvoicesHdr.length() == 0)
                                        Toast.makeText(context, getString(R.string.invoices_not_found) + " " + phone, Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(context, InvoicesHdr.length() + " " + getString(R.string.invoice_found), Toast.LENGTH_LONG).show();

                                    LoadInvoices(InvoicesHdr);
                                    invoicesAdapter.notifyDataSetChanged();
                                    InvoiceshimmerViewContainer.stopShimmer();
                                    InvoiceshimmerViewContainer.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            getInvoicesOnline(phone,context,false);
                            Log.e("InvoicesFragment", "Error fetching invoices: " + error.getMessage());
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

            requestQueue.add(strRequest);
        } else {
            String invoices = InvoicesPreference.getString("Invoices", "");
            if (!invoices.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(invoices);
                    JSONArray InvoicesHdr = jsonResponse.optJSONArray("GetInvoiceHdrResult");
                    if (InvoicesHdr == null) {
                        Toast.makeText(context, "Not available", Toast.LENGTH_LONG).show();
                    } else {
                        if (InvoicesHdr.length() == 0)
                            Toast.makeText(context, getString(R.string.invoices_not_found) + " " + phone, Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, InvoicesHdr.length() + " " + getString(R.string.invoice_found), Toast.LENGTH_LONG).show();

                        LoadInvoices(InvoicesHdr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void LoadInvoices(@NonNull JSONArray invoices) throws JSONException {
        invoices_hdrs.clear();
        try {
            for (int i = 0; i < invoices.length(); i++) {
                JSONObject pp = invoices.getJSONObject(i);
                String inv_no = "#" + pp.optString("inv_no");

                String trn_dt = pp.optString("trn_dt");
                String offer_discount = pp.optString("offer_discount");
                String total_price = pp.optString("total_price").replace(".000", "") + (local.equals("ar") ? " ل.س " : " SP");
                String total_final_price = pp.optString("total_final_price").replace(".000", "") + (local.equals("ar") ? " ل.س " : " SP");
                String loc_name = pp.optString("loc_name");
                Invoice_Hdr inv_hdr = new Invoice_Hdr(inv_no, total_price, total_final_price, offer_discount, "", loc_name, "", trn_dt);
                invoices_hdrs.add(inv_hdr);
            }

            InvoiceRecyclerView.setAdapter(invoicesAdapter);
            invoicesAdapter.notifyDataSetChanged();
            invoicesAdapter.setOnClickListener(new InvoicesAdapter.OnClickListener() {
                @Override
                public void onClick(int position, Invoice_Hdr invoice) {
                    Log.d("InvoicesFragment", "Invoice clicked: " + invoice.getInvoice_no());
                    openDialog(invoice.getInvoice_no());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    public void openDialog(String invoice_no) {
        getInvoiceDetails(context, NetworkStatus.isOnline(context), invoice_no);


    }

    private void getInvoiceDetails(Context context, boolean online, String invoice_no) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/getInvoiceDet/" + invoice_no.substring(1);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        ArrayList<Invoice_Det> invoice_detailses = new ArrayList<>();
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray detResult = jsonResponse.optJSONArray("GetInvoiceDetResult");
                        InvoicesPreference.edit().putString("invoice_det_" + invoice_no, response.toString()).apply();
                        if (detResult == null) {
                            Toast.makeText(context, "not available", Toast.LENGTH_LONG).show();
                        } else {
                            InvoiceDetailsDialog listDialog = new InvoiceDetailsDialog(context, invoice_no) {
                                @Override
                                public void onCreate(Bundle savedInstanceState) {
                                    super.onCreate(savedInstanceState);
                                }
                            };

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(listDialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.dimAmount = 0.7f;
                            listDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    getInvoicesOnline(phone_no, context, false);

                                }
                            });
                            listDialog.show();
                            listDialog.getWindow().setAttributes(lp);
                            listDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            listDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getInvoiceDetails(context,false,invoice_no);
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
            requestQueue.add(strRequest);
        } else {
            Toast.makeText(context, context.getString(R.string.checkwifi), Toast.LENGTH_LONG).show();
        }
    }
    private void insertAPPLog(String destination) {
        SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String UserID = Token.getString("UserID", "");

        // Define the URL for your API endpoint
        String url = UrlEndPoint.General + "service1.svc/insertAPPLog/"+UserID+","+destination;

        // Create a JSONObject to send in the request body

        // Create a Volley request
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response
                        Log.d("Volley", "Notification acknowledgment sent: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("Volley", "Error sending acknowledgment: " + error.getMessage());
                    }
                }
        ){

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
