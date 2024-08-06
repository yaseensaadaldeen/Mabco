package com.mabcoApp.mabco.ui.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mabcoApp.mabco.Classes.NetworkStatus;
import com.mabcoApp.mabco.HttpsTrustManager;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.databinding.FragmentImeiserialBinding;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IMEISERIAL extends Fragment {
    Context context;
    View view;
    FragmentImeiserialBinding fragmentImeiserialBinding;
    EditText myimei;
    Button capture_imei, checkmyanotherdevice;
    TextView resulttv;
    ProgressDialog PD;
    public static final String LINKWEB = UrlEndPoint.General + "Service1.svc/GetVoidWtyBySerial/";
    public static RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentImeiserialBinding = FragmentImeiserialBinding.inflate(inflater, container, false);
        myimei = fragmentImeiserialBinding.myimei;
        capture_imei = fragmentImeiserialBinding.captureImei;
        resulttv = fragmentImeiserialBinding.resulttv;
        checkmyanotherdevice = fragmentImeiserialBinding.checkmyanotherdevice;
        context = getContext();
        try {

            PD = new ProgressDialog(context);
            PD.setMessage("Loading.....");
            PD.setCancelable(false);
            checkmyanotherdevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkStatus.isOnline(context)) {
//                        if (checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 22);
//                            }
//                            return;
//                        }
//
//                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
                        final String SERIALNumber = String.valueOf(myimei.getText());
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        //Find the currently focused view, so we can grab the correct window token from it.

                        //Find the currently focused view, so we can grab the correct window token from it.
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        resulttv.setVisibility(View.VISIBLE);
                        resulttv.setText("Please wait...");
                        queue = Volley.newRequestQueue(context);
                        HttpsTrustManager.allowAllSSL();
                        final String url = LINKWEB + SERIALNumber;

                        StringRequest strRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONArray cars = jsonResponse.optJSONArray("GetVoidWtyBySerialResult");
                                    if (cars == null) {
                                        resulttv.setText("جهازك ليس من مابكو");
                                    } else {

                                        for (int i = 0; i < cars.length(); i++) {
                                            JSONObject car = cars.getJSONObject(i);

                                            String void_wty = car.optString("void_wty");
                                            String wty_end_dt = car.optString("wty_end_dt");


                                            if (void_wty.contains("Y")) {
                                                resulttv.setText("جهازك خارج كفالت مابكو");

                                            } else {
                                                resulttv.setText(wty_end_dt);
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //   Handle Error
                            }
                        });
                        queue.add(strRequest);

                    } else {
                        Toast.makeText(context, getString(R.string.checkwifi), Toast.LENGTH_LONG).show();

                    }
                }
            });
            capture_imei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentIntegrator integrator = new IntentIntegrator(getActivity(context));

                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt("Scan");
                    integrator.setCameraId(0);
                    integrator.setBeepEnabled(false);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
                    integrator.addExtra("SCAN_WIDTH", 800);
                    integrator.addExtra("SCAN_HEIGHT", 200);
                    integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
                    integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");

                }
            });

            Typeface type = ResourcesCompat.getFont(context, R.font.rafat);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fragmentImeiserialBinding.getRoot();

    }


    public void getMethod(final String url) {

        StringRequest strRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray cars = jsonResponse.optJSONArray("GetVoidWtyBySerialResult");
                    if (cars == null) {
                        resulttv.setText("جهازك ليس من مابكو");
                    } else {

                        for (int i = 0; i < cars.length(); i++) {
                            JSONObject car = cars.getJSONObject(i);

                            String void_wty = car.optString("void_wty");
                            String wty_end_dt = car.optString("wty_end_dt");


                            if (void_wty.contains("Y")) {
                                resulttv.setText("جهازك خارج كفالت مابكو");

                            } else {
                                resulttv.setText(wty_end_dt);
                            }


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Handle Error
            }
        });
    }

    public Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }
}