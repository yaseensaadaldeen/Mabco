package com.example.mabco.ui.profile;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.mabco.MainActivity;
import com.example.mabco.R;
import com.example.mabco.UrlEndPoint;
import com.example.mabco.databinding.FragmentProfileBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    RelativeLayout Language_Area, Notification_area, log_area, invoice_Area, policy_Area, warranty_Area, use_terms_Area, about_area,Service_ord_stat_Area;
    LinearLayout account_info;
    Context context;
    private boolean isInitialization = true;
    SharedPreferences PersonalPreference, UserPreferance;
    Spinner Language_spinner;
    SwitchCompat notification_switch;
    private Boolean spinnerTouched = false;
    FloatingActionButton log_icon;
    TextView txt_log;
    String local;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        context = getContext();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE)
            showNavigationBar();
        Language_Area = binding.LanguageArea;
        Language_spinner = binding.LanguageSpinner;
        Notification_area = binding.notificationArea;
        invoice_Area = binding.invoiceArea;
        notification_switch = binding.notificationSwitch;
        log_icon = binding.logIcon;
        log_area = binding.logArea;
        txt_log = binding.txtLog;
        account_info = binding.accountInfo;
        policy_Area = binding.policyArea;
        warranty_Area = binding.warrantyArea;
        use_terms_Area = binding.useTermsArea;
        about_area = binding.aboutArea;
        Service_ord_stat_Area = binding.ServiceOrdStatArea;
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.english_item));
        list.add(getString(R.string.arabic_item));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, list);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        UserPreferance = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        local = PersonalPreference.getString("Language", "ar");

        boolean NotificationStatus = PersonalPreference.getString("NotificationStatus", "enable").equals("enable") ? true : false;
        notification_switch.setChecked(NotificationStatus);
        Language_spinner.setAdapter(adapter);
        Locale currentLocale = getResources().getConfiguration().locale;
        String languageCode = currentLocale.getLanguage();
        Language_spinner.setSelection(languageCode.equals("ar") ? 1 : 0, false);
        Language_spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("Real touch felt.");
                spinnerTouched = true;
                return false;
            }
        });
        if (UserPreferance.getBoolean("Verified", false)) {
            log_icon.setImageResource(R.drawable.baseline_logout_24);
            txt_log.setText(getString(R.string.logout));
            account_info.setVisibility(View.VISIBLE);
        } else {
            log_icon.setImageResource(R.drawable.baseline_login_24);
            txt_log.setText(getString(R.string.Login));
            account_info.setVisibility(View.GONE);
        }
        Language_Area.setOnClickListener(v -> {
            Language_spinner.performClick();
            spinnerTouched = true;
        });
        policy_Area.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_privacyPolicyFragment);
        });
        warranty_Area.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_warrantyPolicyFragment);
        });
        Notification_area.setOnClickListener(v -> {
            notification_switch.performClick();
        });
        Service_ord_stat_Area.setOnClickListener(v->{
            String url = local.equals("ar") ? "https://mabcoonline.com/ar/ServiceCheck.aspx" : "https://mabcoonline.com/ServiceCheck.aspx";
            NavController navController = Navigation.findNavController(binding.getRoot());
            navController.navigate((NavDirections) ProfileFragmentDirections.actionProfileFragmentToWebview().setUrl(url));
        });
        log_area.setOnClickListener(v -> {

            if (UserPreferance.getBoolean("Verified", false)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.logout)
                        .setMessage(R.string.SignoutConfirm)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UserPreferance.edit().clear().commit();
                                log_icon.setImageResource(R.drawable.baseline_logout_24);
                                txt_log.setText(getString(R.string.logout));
                                Toast.makeText(context, R.string.recomend_signin, Toast.LENGTH_LONG);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            } else {
                log_icon.setImageResource(R.drawable.baseline_login_24);
                txt_log.setText(getString(R.string.Login));
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_signInMain);
            }
        });
        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationsPermission(isChecked);
            }
        });
        Language_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnerTouched) {
                    String selectedLanguage = parentView.getItemAtPosition(position).toString();
                    SharedPreferences.Editor editor = PersonalPreference.edit();

                    switch (selectedLanguage) {
                        case "English":
                        case "الانكليزية":
                            setLocale("en");
                            editor.putString("Language", "en");
                            editor.apply();
                            break;
                        case "العربية":
                        case "Arabic":
                            setLocale("ar");
                            editor.putString("Language", "ar");
                            editor.apply();
                            break;
                        // Add more languages if needed
                    }
                }
                spinnerTouched  = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        invoice_Area.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_invoicesFragment);});
        View root = binding.getRoot();
        return root;
    }

    private void NotificationsPermission(Boolean checked) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (checked)
            FirebaseMessaging.getInstance().subscribeToTopic(UrlEndPoint.notification_topic);
        else FirebaseMessaging.getInstance().unsubscribeFromTopic(UrlEndPoint.notification_topic);
        SharedPreferences.Editor editor = PersonalPreference.edit();
        editor.putString("NotificationStatus", checked ? "enable" : "disable");
        editor.apply();
        Toast.makeText(context, checked ? getString(R.string.notification_enable) : getString(R.string.notification_disable), Toast.LENGTH_SHORT).show();
    }

    private void setLocale(String languageCode) {
        Resources standardResources = getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());

        Locale newLocale = new Locale(languageCode);
        config.setLocale(newLocale);
        config.setLayoutDirection(newLocale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Restart the activity to apply changes
        recreate(getActivity());
        restartApp() ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    private void restartApp() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }
    public void showNavigationBar() {
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        navBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}