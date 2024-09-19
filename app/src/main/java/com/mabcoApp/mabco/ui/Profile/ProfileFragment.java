package com.mabcoApp.mabco.ui.Profile;

import static androidx.core.app.ActivityCompat.recreate;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mabcoApp.mabco.Classes.InAppUpdate;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;
import com.mabcoApp.mabco.databinding.FragmentProfileBinding;
import com.mabcoApp.mabco.ui.AppInfo.AppInfoFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    RelativeLayout Language_Area, Notification_area, log_area, invoice_Area, policy_Area, warranty_Area, use_terms_Area, about_area, Service_ord_stat_Area;
    LinearLayout account_info;
    Context context;
    private boolean isInitialization = true;
    InAppUpdate inAppUpdate;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        context = getContext();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav_view);
        if (navBar != null && navBar.getVisibility() == View.INVISIBLE) showNavigationBar();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(false);
        }
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
        Service_ord_stat_Area.setOnClickListener(v -> {
            String url = local.equals("ar") ? "https://mabcoonline.com/ar/ServiceCheck.aspx" : "https://mabcoonline.com/ServiceCheck.aspx";
            NavController navController = Navigation.findNavController(binding.getRoot());
            navController.navigate((NavDirections) ProfileFragmentDirections.actionProfileFragmentToWebview().setUrl(url));
        });
        log_area.setOnClickListener(v -> {

            if (UserPreferance.getBoolean("Verified", false)) {
                new AlertDialog.Builder(context).setTitle(R.string.logout).setMessage(R.string.SignoutConfirm).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UserPreferance.edit().clear().commit();
                        log_icon.setImageResource(R.drawable.baseline_login_24);
                        txt_log.setText(getString(R.string.Login));
                        account_info.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.recomend_signin, Toast.LENGTH_LONG);
                    }
                }).setNegativeButton(android.R.string.no, null).show();

            } else {
                log_icon.setImageResource(R.drawable.baseline_login_24);
                txt_log.setText(getString(R.string.Login));
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_signInMain);
            }
        });
        about_area.setOnClickListener(v -> {
            AppInfoFragment appInfoFragment = new AppInfoFragment();
            appInfoFragment.show(getActivity().getSupportFragmentManager(), "TAG");
           // inAppUpdate.checkForAppUpdate();
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
                spinnerTouched = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        invoice_Area.setOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_profileFragment_to_invoicesFragment);
        });
        View root = binding.getRoot();
        return root;
    }
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Initialize the InAppUpdate class with the parent activity
        inAppUpdate = new InAppUpdate(requireActivity());
    }
    @Override
    public void onResume() {
        super.onResume();
        // Resume the update check if the update is downloaded
        if (inAppUpdate != null) {
            inAppUpdate.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the listener when the view is destroyed
        if (inAppUpdate != null) {
            inAppUpdate.onDestroy();
        }
    }

    private void NotificationsPermission(Boolean checked) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (checked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermissions(true);
            }
            FirebaseMessaging.getInstance().subscribeToTopic(UrlEndPoint.notification_topic);
            SharedPreferences.Editor editor = PersonalPreference.edit();
            editor.putString("isSubscribedToNotification", checked ? "enable" : "disable");
            editor.apply();
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(UrlEndPoint.notification_topic);
            SharedPreferences.Editor editor = PersonalPreference.edit();
            editor.putString("isSubscribedToNotification", checked ? "enable" : "disable");
            editor.apply();
        }
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
        restartApp();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkPermissions(boolean fromSwitch) {

        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.POST_NOTIFICATIONS).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                String isSubscribed = PersonalPreference.getString("isSubscribedToNotification", "disable");
                if (isSubscribed.equals("enable")) {
                    SharedPreferences.Editor editor = PersonalPreference.edit();
                    editor.putString("NotificationStatus", "enable");
                    editor.apply();
                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                SharedPreferences.Editor editor = PersonalPreference.edit();
                editor.putString("NotificationStatus", "disable");
                notification_switch.setChecked(false);
                editor.apply();
                if (fromSwitch) showSettingsDialog();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }

        }).check();
    }

    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // below line is the title for our alert dialog.
        builder.setTitle("بحاجة صلاحية");
        // below line is our message for our dialog
        builder.setMessage(getString(R.string.notification_req));
        builder.setPositiveButton(getString(R.string.go_to_setting), (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
            settingsLauncher.launch(intent);

        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();
    }

    private ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        // User has returned from the settings screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermissions(false);
            }
        }

        // Trigger the switch action if necessary
        notification_switch.performClick();

    });


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