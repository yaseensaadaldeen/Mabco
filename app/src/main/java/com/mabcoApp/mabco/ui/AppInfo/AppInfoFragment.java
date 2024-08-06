package com.mabcoApp.mabco.ui.AppInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.mabcoApp.mabco.R;

public class AppInfoFragment extends BottomSheetDialogFragment {

    private static final int UPDATE_REQUEST_CODE = 500;

    private TextView versionTextView;
    private Button updateButton;
    private AppUpdateManager appUpdateManager;
    SharedPreferences PersonalPreference;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_info, container, false);
        versionTextView = view.findViewById(R.id.versionTextView);
        context = getContext();
        updateButton = view.findViewById(R.id.updatebutton);
        PersonalPreference = context.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
        displayAppVersion();
        appUpdateManager = AppUpdateManagerFactory.create(requireContext());

        String update_required = PersonalPreference.getString("last_version", "true");
        if (update_required.equals("false")) checkForUpdates();
        else {
            updateButton.setVisibility(View.GONE);
        }


        return view;
    }

    private void displayAppVersion() {
        try {
            String versionName = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName;
            versionTextView.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionTextView.setText("Version not found");
        }
    }

    private void checkForUpdates() {

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                updateButton.setVisibility(View.VISIBLE);
                updateButton.setOnClickListener(v -> startUpdateFlow(appUpdateInfo));
            } else {
                updateButton.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to check for updates", Toast.LENGTH_SHORT).show();
            updateButton.setOnClickListener(v -> openAppInPlayStore());
            // openAppInPlayStore();
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, requireActivity(), UPDATE_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resume the update if needed
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, requireActivity(), UPDATE_REQUEST_CODE);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Failed to resume update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openAppInPlayStore() {
        //final String appPackageName = getPackageName(); // Get the current app package name
        final String appPackageName = getActivity().getPackageName(); // Get the current app package name

        try {
            // Try to open the app page in Google Play app
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // If Google Play is not installed, open in browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}