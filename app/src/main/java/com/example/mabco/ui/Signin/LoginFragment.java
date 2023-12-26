package com.example.mabco.ui.Signin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mabco.Classes.NetworkStatus;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginFragment extends Fragment {
    View view;
    Context context;
    SharedPreferences UserData;
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
        requestQueue = Volley.newRequestQueue(this.getContext());
        login_button = view.findViewById(R.id.login_button);
        login_button.setOnClickListener(v -> {
            Login(NetworkStatus.isOnline(context));
        });
        return view;
    }

    public void Login(boolean online) {
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
                com.example.mabco.HttpsTrustManager.allowAllSSL();
                String url = UrlEndPoint.General + "Service1.svc/MabcoApp_Signup_Validate/";
                url = url + login_user_name.getText() + "," + login_password.getText() + ",ar";
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray array = jsonResponse.optJSONArray("MabcoApp_SignupValidateResult");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("something went wrong").setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }) ;
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
}