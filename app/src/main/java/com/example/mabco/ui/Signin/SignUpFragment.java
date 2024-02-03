package com.example.mabco.ui.Signin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mabco.Classes.NetworkStatus;
import com.example.mabco.MainActivity;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;
import com.example.mabco.databinding.FragmentSignUpBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {
    Context context;
    private FragmentSignUpBinding SignUpbinding;
    public RequestQueue requestQueue;
    View view;
    SharedPreferences preferences;
    EditText EdTxt_user_name, EdTxt_password, EdTxt_confirm_password, EdTxt_phone_no, EdTxt_verification_Code;
    TextView txt_user_name_error, txt_password_error, txt_confirm_password_error, txt_phone_no_error, message;
    Button btn_Signup, btn_next, btn_back;
    LinearLayout signup_phone_verification, SignUpLayout;
    TextView txt_Timer;
    //required validator error RV
    boolean error_phoneRV, error_user_nameRV, error_passwordRV, error_confirm_passwordRV;
    private SignUpManager signUpManager;
    private CountDownTimer countDownTimer;
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

        signup_phone_verification = view.findViewById(R.id.signup_phone_verification);
        SignUpLayout = view.findViewById(R.id.SignUpLayout);
        EdTxt_user_name = view.findViewById(R.id.signup_user_name);
        EdTxt_phone_no = view.findViewById(R.id.signup_phone_no);
        EdTxt_password = view.findViewById(R.id.signup_password);
        EdTxt_confirm_password = view.findViewById(R.id.signup_confirm);
        txt_Timer = view.findViewById(R.id.txt_Timer);
        txt_user_name_error = view.findViewById(R.id.signup_user_name_error);
        txt_phone_no_error = view.findViewById(R.id.signup_phone_no_error);
        txt_password_error = view.findViewById(R.id.signup_password_error);
        txt_confirm_password_error = view.findViewById(R.id.signup_confirm_error);
        message = view.findViewById(R.id.message);
        signUpManager = new SignUpManager(context);
        btn_next = view.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(v -> {
            // signUpManager.resetAttempts();
            if (signUpManager.canSignUp()) {
                // Update the sign-up attempts
                signUpManager.signUp();
                SignUpValidateApi(NetworkStatus.isOnline(context));
            } else {
                // User cannot sign up at the moment
                long remainingTime = signUpManager.getRemainingTime();
                showRemainingTimeDialog(remainingTime);
            }
        });

        addTextChangedListenerToEditTexts();
        return view;
    }

    public void SignUpValidateApi(Boolean online) {
        ErrorHandling();
        if (!PageValid) {
            message.setText(getString(R.string.NotValid));
            message.setVisibility(View.VISIBLE);
            return;
        } else message.setVisibility(View.GONE);

        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.example.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/MabcoApp_Signup_Validate/";
            url = url + EdTxt_user_name.getText() + "," + EdTxt_phone_no.getText() + ","  + EdTxt_password.getText() + ",ar";
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("MabcoApp_SignupValidateResult");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject arrayObj = array.getJSONObject(i);
                                String result = arrayObj.getString("result");
                                if (result.equals("Success")) {
                                    String verificationCode = arrayObj.getString("verificationCode");
                                    String verificationCodeExpireDate = arrayObj.getString("verificationCodeExpireDate");
                                    String validMinutes = arrayObj.getString("validMinutes");
                                    //verification procces
                                    PhoneNoVerification(verificationCode, verificationCodeExpireDate, validMinutes);

                                } else if (result.equals("Data_Error")) {
                                    // Transaction failed, handle the error messages
                                    String phoneError = arrayObj.optString("phone_error");
                                    String userNameError = arrayObj.optString("user_name_error");
                                    String error_msg = arrayObj.optString("error_msg");
                                    if (!phoneError.isEmpty())
                                        ErrorMode(EdTxt_phone_no, txt_phone_no_error, true, phoneError);
                                    if (!userNameError.isEmpty())
                                        ErrorMode(EdTxt_user_name, txt_user_name_error, true, userNameError);
                                    if (!error_msg.isEmpty()) {
                                        message.setText(error_msg);
                                        message.setTextColor(Color.RED);
                                        message.setVisibility(View.VISIBLE);
                                    }
                                } else if (result.equals("Verification_Error")) {
                                    String ErrorMessage = arrayObj.optString("message");
                                    message.setText(ErrorMessage);
                                    message.setTextColor(Color.RED);
                                    message.setVisibility(View.VISIBLE);
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
                    builder.setMessage("something went wrong").setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
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
            builder.setMessage(getString(R.string.checkwifi)).setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void SignUpSubmitApi(Boolean online, String verificationCodeExpireDate, String verificationCode) {
        if (online) {
            requestQueue = Volley.newRequestQueue(context);
            com.example.mabco.HttpsTrustManager.allowAllSSL();
            String url = UrlEndPoint.General + "Service1.svc/MabcoApp_Signup_insert/";
            url = url + EdTxt_user_name.getText() + "," + EdTxt_phone_no.getText() + "," + EdTxt_password.getText() + "," + verificationCodeExpireDate + "," + verificationCode + ",ar";
            StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray array = jsonResponse.optJSONArray("MabcoApp_Signup_insertResult");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject arrayObj = array.getJSONObject(i);
                                String result = arrayObj.getString("result");
                                String custm_Code =  arrayObj.getString("custm_code");
                                if (result.equals("1")) {
                                    showSuccessDialog();
                                }
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("something went wrong").setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                            AlertDialog dialog = builder.create();
                            dialog.show();
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
            builder.setMessage(getString(R.string.checkwifi)).setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void PhoneNoVerification(String verificationCode, String verificationCodeExpireDate, String validMinutes) {

        signup_phone_verification.setVisibility(View.VISIBLE);
        SignUpLayout.setVisibility(View.GONE);
        btn_Signup = view.findViewById(R.id.signup_button);
        btn_back = view.findViewById(R.id.btn_back);
        btn_Signup.setEnabled(true);
        btn_back.setOnClickListener(n -> {
            signup_phone_verification.setVisibility(View.GONE);
            SignUpLayout.setVisibility(View.VISIBLE);
        });

        TextView txt_resend_code = view.findViewById(R.id.txt_resend_code);
        EdTxt_verification_Code = view.findViewById(R.id.EdTxt_verification_Code);
        txt_resend_code.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        txt_Timer.setText("");
        txt_resend_code.setOnClickListener(v -> {
            if (signUpManager.canSignUp()) {
                // Update the sign-up attempts
                signUpManager.signUp();
                SignUpValidateApi(NetworkStatus.isOnline(context));
            } else {
                // User cannot sign up at the moment
                long remainingTime = signUpManager.getRemainingTime();
                showRemainingTimeDialog(remainingTime);
            }
        });
        startTimer(validMinutes);
        btn_Signup.setOnClickListener(n -> {
            String enterd_code = String.valueOf(EdTxt_verification_Code.getText());
            if (enterd_code.equals(verificationCode)) {
                if (signUpManager.canSignUp()) {
                    signUpManager.signUp();
                    SignUpSubmitApi(NetworkStatus.isOnline(context), verificationCodeExpireDate, verificationCode);
                    Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("UserName", String.valueOf(EdTxt_user_name.getText()));
                    editor.putString("PhoneNO", String.valueOf(EdTxt_phone_no.getText()));
                    editor.putString("Password", String.valueOf(EdTxt_password.getText()));
                    editor.putBoolean("Verified", true);
                    editor.apply();
                    ((MainActivity) getActivity()).updateUserName(String.valueOf(EdTxt_user_name.getText()));
                } else {
                    // User cannot sign up at the moment
                    long remainingTime = signUpManager.getRemainingTime();
                    showRemainingTimeDialog(remainingTime);
                }
            } else {
                EdTxt_verification_Code.setError(getString(R.string.Verification_resend));
            }

        });
    }
    private void startTimer(String validMinutes) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(TimeUnit.MINUTES.toMillis(Integer.parseInt(validMinutes)), 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                // Convert milliseconds into hour, minute, and seconds
                String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
                        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                txt_Timer.setText(hms); // Set text
            }

            public void onFinish() {
                txt_Timer.setText("FINISH!!");
                Toast.makeText(context, "Your mobile verification code has expired", Toast.LENGTH_LONG).show();
                btn_Signup.setEnabled(false);
            }
        }.start();
    }
    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void ErrorHandling() {
        String user_name = String.valueOf(EdTxt_user_name.getText()), password = String.valueOf(EdTxt_password.getText()), confirm = String.valueOf(EdTxt_confirm_password.getText()), phone_no = String.valueOf(EdTxt_phone_no.getText());
        if (user_name.isEmpty()) {
            ErrorMode(EdTxt_user_name, txt_user_name_error, true, getString(R.string.MustFillError));
            error_user_nameRV = true;
        } else {
            error_user_nameRV = false;
            ErrorMode(EdTxt_user_name, txt_user_name_error, false, "");
        }

        if (phone_no.isEmpty()) {
            ErrorMode(EdTxt_phone_no, txt_phone_no_error, true, getString(R.string.MustFillError));
            error_phoneRV = true;
        } else {
            ErrorMode(EdTxt_phone_no, txt_phone_no_error, false, "");
            error_phoneRV = false;
        }
        if (password.isEmpty()) {
            ErrorMode(EdTxt_password, txt_password_error, true, getString(R.string.MustFillError));
            error_passwordRV = true;
        } else {
            ErrorMode(EdTxt_password, txt_password_error, false, "");
            error_passwordRV = false;
        }

        if (confirm.isEmpty()) {
            ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, true, getString(R.string.MustFillError));
            error_confirm_passwordRV = true;
        } else {
            ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, false, "");
            error_confirm_passwordRV = false;
        }
    }
    private void addTextChangedListenerToEditTexts() {
        EdTxt_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String user_name = String.valueOf(EdTxt_user_name.getText());
                if (user_name.isEmpty()) {
                    error_user_nameRV = true;
                    ErrorMode(EdTxt_user_name, txt_user_name_error, true, getString(R.string.MustFillError));
                }
                String username = String.valueOf(EdTxt_user_name.getText());

                if (username.length() < 6)  ErrorMode(EdTxt_user_name, txt_user_name_error, true, getString(R.string.userLengthCheck));
                else ErrorMode(EdTxt_user_name, txt_user_name_error, false, "");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (error_user_nameRV) {
                    ErrorMode(EdTxt_user_name, txt_user_name_error, false, "");
                    error_user_nameRV = false;
                }
            }
        });
        EdTxt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String password = String.valueOf(EdTxt_password.getText());
                String confirmpassword = String.valueOf(EdTxt_confirm_password.getText());
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

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (error_passwordRV) {
                    ErrorMode(EdTxt_password, txt_password_error, false, "");
                    error_passwordRV = false;
                }
            }
        });
        EdTxt_confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String password = String.valueOf(EdTxt_password.getText());
                String Confirm = String.valueOf(EdTxt_confirm_password.getText());
                if (!password.equals(Confirm))
                    ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, true, getString(R.string.PasswordConfirmError));
                else ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, false, "");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (error_confirm_passwordRV) {
                    ErrorMode(EdTxt_confirm_password, txt_confirm_password_error, false, "");
                    error_confirm_passwordRV = false;
                }
            }
        });
        EdTxt_phone_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (error_phoneRV) {
                    ErrorMode(EdTxt_phone_no, txt_phone_no_error, false, "");
                    error_confirm_passwordRV = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String phoneNo = EdTxt_phone_no.getText().toString().trim();
                if (!isValidPhoneNo(phoneNo)) {
                    ErrorMode(EdTxt_phone_no, txt_phone_no_error, true, "Invalid phone number");
                } else {
                    ErrorMode(EdTxt_phone_no, txt_phone_no_error, false, "");

                }
            }
        });
    }
    private void showSuccessDialog() {
        try {
            signup_phone_verification.setVisibility(View.GONE);
            SignUpLayout.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.signup_success_dialog, null);
            Button backToHomeButton = dialogView.findViewById(R.id.backToHomeButton);
            Button continueShoppingButton = dialogView.findViewById(R.id.continueShoppingButton);
            ImageButton Cancel = dialogView.findViewById(R.id.btnCancel);

            builder.setView(dialogView);
            builder.setCancelable(false); // Prevent the user from dismissing the dialog by clicking outside

            final AlertDialog alertDialog = builder.create();


            // Set click listeners for buttons
            backToHomeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                        NavController navController = navHostFragment.getNavController();
                        navController.popBackStack(R.id.nav_home, false);
                        navController.navigate(R.id.nav_home);
                        alertDialog.dismiss();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            continueShoppingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                        NavController navController = navHostFragment.getNavController();
                        navController.popBackStack();
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                        NavController navController = navHostFragment.getNavController();
                        navController.popBackStack(R.id.nav_home, false);
                        navController.navigate(R.id.nav_home);
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = 1400;
            alertDialog.show();
            alertDialog.getWindow().setAttributes(lp);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showRemainingTimeDialog(long remainingTimeMillis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Please wait");
        // builder.setMessage("You must wait before trying again.\nRemaining time: " + formatTime(remainingTimeMillis));
        builder.setMessage("You must wait before trying again.please try again tomorrow ");
        builder.setNegativeButton(getString(R.string.continue_shopping_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                NavController navController = navHostFragment.getNavController();
                navController.popBackStack();
                dialogInterface.dismiss(); // This dismisses the dialog
            }
        });
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // This dismisses the dialog
            }
        });
        builder.create().show();
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
    private boolean isValidPhoneNo(String phoneNo) {
        String phoneNoPattern = "^[0-9]{10}$"; // This is a simple pattern for 10-digit phone numbers
        return phoneNo.matches(phoneNoPattern);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SignUpbinding = null;
    }
}