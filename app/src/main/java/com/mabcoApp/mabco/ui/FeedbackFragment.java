package com.mabcoApp.mabco.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mabcoApp.mabco.HttpsTrustManager;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private EditText desc;
    private Button submit;
    Context context;
    View view;
    RequestQueue queue;
    SharedPreferences UserPreferance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        context = getContext();
        desc = view.findViewById(R.id.feedbackInput);
        submit = view.findViewById(R.id.sendFeedbackButton);
        final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);

        NavController navController = navHostFragment.getNavController();
        UserPreferance = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        if (!UserPreferance.getBoolean("Verified", false) || UserPreferance.getString("PhoneNO", "").equals("")) {
            Toast.makeText(context, R.string.recomend_signin, Toast.LENGTH_LONG).show();
            navController.navigate(R.id.action_feedbackFragment_to_signInMain);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = desc.getText().toString().trim();
                if (!feedback.isEmpty()) {
                    sendFeedback(feedback);
                } else {
                    Toast.makeText(context, "Please enter your feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });
        insertAPPLog("Feedback page");
        queue = Volley.newRequestQueue(context);
        showNavigationBar();
        return view;
    }

    private void sendFeedback(String descr) {

        submit.setEnabled(false);
        String name = UserPreferance.getString("UserName", "");
        String phone = UserPreferance.getString("PhoneNO", "");

        /*if (name.isEmpty()) {
            name = "empty";
        }*/
        HttpsTrustManager.allowAllSSL();
        String url = UrlEndPoint.General + "Service1.svc/feedBack/" + name + "," + phone + "," + descr;
        StringRequest strRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                submit.setEnabled(true);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray cars;
                    cars = jsonResponse.optJSONArray("GetFeedBackResult");
                    if (cars.getJSONObject(0).getString("note") != null) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle(getString(R.string.done));
                        alertDialog.setMessage(cars.getJSONObject(0).getString("note"));
                        alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialog.show();
                        Toast.makeText(context, cars.getJSONObject(0).getString("note"), Toast.LENGTH_SHORT).show();
                        desc.setText("");
                    } else {
                        Toast.makeText(context, "error , try again later", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    submit.setEnabled(true);
                    {
                        Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
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

        strRequest.setRetryPolicy(new DefaultRetryPolicy(25000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(strRequest);
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
    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}