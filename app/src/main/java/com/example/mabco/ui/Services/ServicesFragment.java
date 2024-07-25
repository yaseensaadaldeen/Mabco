package com.example.mabco.ui.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.mabco.R;

public class ServicesFragment extends Fragment {
    CardView InstallmentCard, WarrantyCard, PersonalServiceCard, EpaymentCard, ApplicationCard, printservicesCard;
    ImageView InstallmentImage, WarrantyImage, PersonalServiceImage, EpaymentImage, ApplicationImage, printservicesImage;
    TextView InstallmentText, WarrantyText, ServiceOrderText, PersonalServiceText, EpaymentText, ApplicationText, printservicesText;
    NavController navController;
    SharedPreferences PersonalPreference;
    String local;


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
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        final Context context = this.getContext();
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
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_services_to_IMEISERIAL);
                navController.navigateUp();
                navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToIMEISERIAL());
            }
        });

        PersonalServiceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_services_to_IMEISERIAL);
                navController.navigateUp();
                navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToPersonalServicesFragment());
            }
        });
        printservicesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_services_to_webview);
                navController.navigateUp();
                String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/Others.aspx" : "https://www.mabcoonline.com/Others.aspx";
                navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));

            }
        });
        InstallmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_services_to_webview);
                navController.navigateUp();
                String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/Installment.aspx" : "https://www.mabcoonline.com/Installment.aspx";
                navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));

            }
        });
        ApplicationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_services_to_webview);
                navController.navigateUp();
                String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/Applications.aspx" : "https://www.mabcoonline.com/Applications.aspx";
                navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));

            }
        });

        EpaymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController = Navigation.findNavController(view);
                Navigation.findNavController(view).navigate(R.id.action_nav_services_to_webview);
                navController.navigateUp();
                String url = local.equals("ar") ? "https://www.mabcoonline.com/ar/EPayment.aspx" : "https://www.mabcoonline.com/EPayment.aspx";
                navController.navigate((NavDirections) ServicesFragmentDirections.actionNavServicesToWebview().setUrl(url));

            }
        });

        return view;
    }
}