package com.mabcoApp.mabco.ui.Signin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    View view;
    Context context;
    SharedPreferences UserData,Token;
    EditText login_user_name, login_password;
    TextView login_password_error, login_user_name_error, Login_message;
    public RequestQueue requestQueue;
    Button login_button;
    boolean error_user_nameRV, error_passwordRV;
    boolean PageValid = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        context = getContext();
        login_user_name = view.findViewById(R.id.login_user_name);
        login_password = view.findViewById(R.id.login_password);
        login_password_error = view.findViewById(R.id.login_password_error);
        login_user_name_error = view.findViewById(R.id.login_user_name_error);
        Login_message = view.findViewById(R.id.Login_message);
        UserData = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this.getContext());
        login_button = view.findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            try {
                Login(NetworkStatus.isOnline(context));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        insertAPPLog("Login Page");
        return view;
    }

    public void Login(boolean online) throws Exception {
        String Cashed_User_name = UserData.getString("user_name", ""), Cashed_Password = UserData.getString("password", "");
        ErrorHandling();
        if (!PageValid) {
            Login_message.setText(getString(R.string.NotValid));
            Login_message.setVisibility(View.VISIBLE);
            return;
        } else Login_message.setVisibility(View.GONE);

        if (Cashed_User_name.isEmpty() || Cashed_Password.isEmpty()) {
            if (online) {
                requestQueue = Volley.newRequestQueue(context);

                com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
                String url = UrlEndPoint.General + "Service1.svc/MabcoApp_Login/";
                String encoded_password = URLEncoder.encode(String.valueOf(login_password.getText()), "UTF-8");
                String EncodedToken = URLEncoder.encode(Token.getString("Token", ""), "UTF-8");
                url = url + login_user_name.getText() + ","+ encoded_password + ","+EncodedToken;
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = new JSONArray(response);
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject arrayObj = array.getJSONObject(i);
                                    String result = arrayObj.getString("result");
                                    if (result.equals("success")) {

                                        String custm_name = arrayObj.getString("custm_name");
                                        String PhoneNO = arrayObj.getString("phone1");
                                        SharedPreferences.Editor editor = UserData.edit();
                                        editor.putString("UserName", custm_name);
                                        editor.putString("PhoneNO",PhoneNO);
                                        editor.putString("Password", String.valueOf(login_password.getText()));
                                        editor.putBoolean("Verified", true);
                                        editor.apply();
                                        final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                                        NavController navController = navHostFragment.getNavController();
                                        navController.popBackStack();
                                        Toast.makeText(context, getString(R.string.login_succeded)+" " +custm_name, Toast.LENGTH_SHORT).show();
                                    }
                                    else if(result.equals("failed"))
                                    {
                                        Toast.makeText(context, getString(R.string.login_failed) , Toast.LENGTH_SHORT).show();
                                    }
                                }
                                }
                            } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(getString(R.string.server_error)).setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                        AlertDialog dialog = builder.create();
                        dialog.show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.checkwifi)).setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        else
        {
            final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            NavController navController = navHostFragment.getNavController();
            navController.popBackStack(R.id.nav_home, false);
            navController.navigate(R.id.nav_home);

        }
    }

    public void ErrorMode(EditText editText, TextView errorTextView, boolean iserror, String errortext) {
        if (iserror) {
            editText.setBackgroundResource(R.drawable.textedit_error_shape);
            editText.setError(errortext);
            PageValid = false;
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(errortext);
        } else {
            editText.setBackgroundResource(R.drawable.textedit_shape);
            PageValid = true;
            errorTextView.setVisibility(View.GONE);
            errorTextView.setText("");
        }
    }

    private void ErrorHandling() {
        String user_name = String.valueOf(login_user_name.getText()), password = String.valueOf(login_password.getText());
        if (user_name.isEmpty()) {
            ErrorMode(login_user_name, login_user_name_error, true, getString(R.string.MustFillError));
            error_user_nameRV = true;
        } else {
            error_user_nameRV = false;
            ErrorMode(login_user_name, login_user_name_error, false, "");
        }

        if (password.isEmpty()) {
            ErrorMode(login_password, login_password_error, true, getString(R.string.MustFillError));
            error_passwordRV = true;
        } else {
            ErrorMode(login_password, login_password_error, false, "");
            error_passwordRV = false;
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