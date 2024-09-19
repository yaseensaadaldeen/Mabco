package com.mabcoApp.mabco.ui.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import java.util.HashMap;
import java.util.Map;

public class ServicesFragment extends Fragment {
    CardView InstallmentCard, WarrantyCard, PersonalServiceCard, EpaymentCard, ApplicationCard, printservicesCard;
    ImageView InstallmentImage, WarrantyImage, PersonalServiceImage, EpaymentImage, ApplicationImage, printservicesImage;
    TextView InstallmentText, WarrantyText, ServiceOrderText, PersonalServiceText, EpaymentText, ApplicationText, printservicesText;
    NavController navController;
    View view;
    SharedPreferences PersonalPreference;
    String local;
    Context context;


    public ServicesFragment() {
        // Required empty public constructor
    }

    public static ServicesFragment init(Context context) {
        ServicesFragment homeFragment = new ServicesFragment();
        return homeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_services, container, false);
          context = this.getContext();

        InstallmentCard = view.findViewById(R.id.InstallmentCard);
        WarrantyCard = view.findViewById(R.id.WarrantyCard);
        PersonalServiceCard = view.findViewById(R.id.PersonalServiceCard);
        EpaymentCard = view.findViewById(R.id.EpaymentCard);
        ApplicationCard = view.findViewById(R.id.ApplicationCard);
        printservicesCard = view.findViewById(R.id.printservicesCard);

        InstallmentImage = view.findViewById(R.id.InstallmentImage);
        WarrantyImage = view.findViewById(R.id.WarrantyImage);
        PersonalServiceImage = view.findViewById(R.id.PersonalServiceImage);
        EpaymentImage = view.findViewById(R.id.EpaymentImage);
        ApplicationImage = view.findViewById(R.id.ApplicationImage);
        printservicesImage = view.findViewById(R.id.printservicesImage);

        Glide.with(context).load("https://mabcoonline.com/images/services/installment.png").fitCenter().centerInside().into(InstallmentImage);
        Glide.with(context).load("https://mabcoonline.com/images/services/customerservice.png").fitCenter().centerInside().into(PersonalServiceImage);
        Glide.with(context).load("https://mabcoonline.com/images/services/Epayment.png").fitCenter().centerInside().into(EpaymentImage);
        Glide.with(context).load("https://mabcoonline.com/images/services/Application.png").fitCenter().centerInside().into(ApplicationImage);
        Glide.with(context).load("https://mabcoonline.com/images/services/Warranty.png").fitCenter().centerInside().into(WarrantyImage);
        Glide.with(context).load("https://mabcoonline.com/images/services/printservices.png").fitCenter().centerInside().into(printservicesImage);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");
        WarrantyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              navigate("verfy_warranty");
            }
        });

        PersonalServiceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate("personal_service");
            }
        });
        printservicesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate("print_service");
            }
        });
        InstallmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate("installment");
            }
        });
        ApplicationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate("app");
            }
        });

        EpaymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigate("payment");
            }
        });
        if (getArguments() != null) {
            String type = getArguments().getString("type");
            if (type == null) {
                Toast.makeText(getContext(), "Error: Page not found!", Toast.LENGTH_SHORT).show();
                // Navigate back
                requireActivity().onBackPressed();
            } else {
                navigate(type);
                setArguments(null);
            }
        }
        if (savedInstanceState == null){
            insertAPPLog("Services Page");
        }
        return view;
    }

    public void navigate(String type) {
        try {
            navController = NavHostFragment.findNavController(this);
            switch (type) {
                case "payment": {
                    insertAPPLog("Payment Page");
                    String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/EPayment.aspx" : "https://www.mabcoonline.com/EPayment.aspx";
                    navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));
                    break;
                }
                case "app": {
                    insertAPPLog("Apps Page");
                    Navigation.findNavController(view).navigate(R.id.action_nav_services_to_webview);
                    String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/Applications.aspx" : "https://www.mabcoonline.com/Applications.aspx";
                    navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));
                    break;
                }
                case "verfy_warranty":
                    insertAPPLog("Device Warranty Page");
                    navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToIMEISERIAL());
                    break;
                case "personal_service":
                    insertAPPLog("Personal Services Page");
                    navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToPersonalServicesFragment());
                    break;
                case "installment": {
                    insertAPPLog("Installment page");
                    String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/Installment.aspx" : "https://www.mabcoonline.com/Installment.aspx";
                    navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));
                    break;
                }
                case "print_service": {
                    insertAPPLog("Print Service Page");
                    String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/Others.aspx" : "https://www.mabcoonline.com/Others.aspx";
                    navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));
                    break;
                }
                default:break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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