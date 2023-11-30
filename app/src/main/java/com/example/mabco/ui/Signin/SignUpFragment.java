package com.example.mabco.ui.Signin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.mabco.databinding.FragmentSignUpBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

public class SignUpFragment extends Fragment {
    Context context;
    private FragmentSignUpBinding SignUpbinding;
    public RequestQueue requestQueue;
    View view;
    SharedPreferences preferences;
    EditText EdTxt_user_name, EdTxt_email, EdTxt_password, EdTxt_confirm_password, EdTxt_phone_no;
    TextView txt_user_name_error, txt_email_error, txt_password_error, txt_confirm_password_error, txt_phone_no_error, message;
    Button btn_Signup;
    boolean PageValid = false;

    public SignUpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SignUpbinding = FragmentSignUpBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        context = getContext();
        preferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this.getContext());
        hide();

        EdTxt_user_name = view.findViewById(R.id.signup_user_name);
        EdTxt_phone_no = view.findViewById(R.id.signup_phone_no);
//        EdTxt_email = view.findViewById(R.id.signup_email);
        EdTxt_password = view.findViewById(R.id.signup_password);
        EdTxt_confirm_password = view.findViewById(R.id.signup_confirm);

        txt_user_name_error = view.findViewById(R.id.signup_user_name_error);
        txt_phone_no_error = view.findViewById(R.id.signup_phone_no_error);
//        txt_email_error = view.findViewById(R.id.signup_email_error);
        txt_password_error = view.findViewById(R.id.signup_password_error);
        txt_confirm_password_error = view.findViewById(R.id.signup_confirm_error);
        message = view.findViewById(R.id.message);

        btn_Signup = view.findViewById(R.id.signup_button);
        btn_Signup.setOnClickListener(v -> SignUpApi(NetworkStatus.isOnline(context)));


//
        EdTxt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = String.valueOf(EdTxt_password.getText());
                if (password.length() < 6)
                    ErrorMode(EdTxt_password, txt_password_error, true, getString(R.string.PasswordLengthCheck));
                else if (!password.matches(".*\\d.*"))
                    ErrorMode(EdTxt_password, txt_password_error, true, getString(R.string.PasswordDegitCheck));
                else if (!password.matches(".*[a-zA-Z].*"))
                    ErrorMode(EdTxt_password, txt_password_error, true, getString(R.string.PasswordLetterCheck));
                else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))
                    ErrorMode(EdTxt_password, txt_password_error, true, getString(R.string.PasswordSpecialCharCheck));
                else ErrorMode(EdTxt_password, txt_password_error, false, "");
            }
        });
        EdTxt_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = String.valueOf(EdTxt_password.getText());
                String Confirm = String.valueOf(EdTxt_confirm_password.getText());
                if (!password.equals(Confirm))
                    ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, true, getString(R.string.PasswordConfirmError));
            }
        });
        return view;
    }

    public void SignUpApi(Boolean online) {
        ErrorHandling();
        if (!PageValid) {
            message.setText(getString(R.string.NotValid));
            message.setVisibility(View.VISIBLE);
            return;
        } else message.setVisibility(View.GONE);

        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.example.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/MabcoApp_Signup/";
            url = url + EdTxt_user_name.getText() + "," + EdTxt_phone_no.getText() + ","  + EdTxt_password.getText() + ",ar";
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("MabcoApp_SignupResult");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject arrayObj = array.getJSONObject(i);
                                String result = arrayObj.getString("result");
                                if (result.equals("Success")) {
                                    message.setText(getString(R.string.SignUpSuccess));
                                    message.setTextColor(Color.GREEN);
                                    message.setVisibility(View.VISIBLE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("UserName", String.valueOf(EdTxt_user_name.getText()));
                                    editor.putString("PhoneNO", String.valueOf(EdTxt_phone_no.getText()));
//                                    editor.putString("Email", String.valueOf(EdTxt_email.getText()));
                                    editor.putString("Password", String.valueOf(EdTxt_password.getText()));
                                    editor.apply();
                                } else {
                                    // Transaction failed, handle the error messages
                                    String phoneError = arrayObj.optString("phone_error");
                                    String userNameError = arrayObj.optString("user_name_error");
//                                    String emailError = arrayObj.optString("email_error");
                                    String error_msg = arrayObj.optString("error_msg");
                                    if (!phoneError.isEmpty())
                                        ErrorMode(EdTxt_phone_no, txt_phone_no_error, true, phoneError);
                                    if (!userNameError.isEmpty())
                                        ErrorMode(EdTxt_user_name, txt_user_name_error, true, userNameError);
//                                    if (!emailError.isEmpty())
//                                        ErrorMode(EdTxt_email, txt_email_error, true, emailError);
                                    if (!error_msg.isEmpty()) {
                                        message.setText(error_msg);
                                        message.setTextColor(Color.RED);
                                        message.setVisibility(View.VISIBLE);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("something went wrong")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("something went wrong")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.checkwifi))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void ErrorHandling() {
        String user_name = String.valueOf(EdTxt_user_name.getText()),
                password = String.valueOf(EdTxt_password.getText()),
//                email = String.valueOf(EdTxt_email.getText()),
                confirm = String.valueOf(EdTxt_confirm_password.getText()),
                phone_no = String.valueOf(EdTxt_phone_no.getText());
        if (user_name.isEmpty())
            ErrorMode(EdTxt_user_name, txt_user_name_error, true, getString(R.string.MustFillError));
        else ErrorMode(EdTxt_user_name, txt_user_name_error, false, "");

        if (phone_no.isEmpty())
            ErrorMode(EdTxt_phone_no, txt_phone_no_error, true, getString(R.string.MustFillError));
        else ErrorMode(EdTxt_phone_no, txt_phone_no_error, false, "");

//        if (email.isEmpty())
//            ErrorMode(EdTxt_email, txt_email_error, true, getString(R.string.MustFillError));
//        else ErrorMode(EdTxt_email, txt_email_error, false, "");

        if (password.isEmpty())
            ErrorMode(EdTxt_password, txt_password_error, true, getString(R.string.MustFillError));
        else ErrorMode(EdTxt_password, txt_password_error, false, "");

        if (confirm.isEmpty())
            ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, true, getString(R.string.MustFillError));
        else ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, false, "");


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

    public void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.INVISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    public void show() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private ActionBar getSupportActionBar() {
        ActionBar actionBar = null;
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            actionBar = activity.getSupportActionBar();
        }
        return actionBar;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        show();
        SignUpbinding = null;
    }

    private void addTextChangedListenerToEditTexts() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do something before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do something after text changes
            }
        };
        EdTxt_user_name.addTextChangedListener(textWatcher);
        EdTxt_password.addTextChangedListener(textWatcher);
        EdTxt_phone_no.addTextChangedListener(textWatcher);
        EdTxt_confirm_password.addTextChangedListener(textWatcher);

    }
}