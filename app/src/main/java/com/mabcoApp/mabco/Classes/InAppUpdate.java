package com.mabcoApp.mabco.Classes;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.mabcoApp.mabco.R;

public class InAppUpdate {


    private final Activity parentActivity;

    private final AppUpdateManager appUpdateManager;

    private final int appUpdateType = AppUpdateType.IMMEDIATE;
    private final int MY_REQUEST_CODE = 500;

    public InAppUpdate(Activity activity) {
        this.parentActivity = activity;
        appUpdateManager = AppUpdateManagerFactory.create(parentActivity);
    }

    InstallStateUpdatedListener stateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate();
        }
    };

    public void checkForAppUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(info -> {
            boolean isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE;
            boolean isUpdateAllowed = info.isUpdateTypeAllowed(appUpdateType);

            if (isUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            info,
                            appUpdateType,
                            parentActivity,
                            MY_REQUEST_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        appUpdateManager.registerListener(stateUpdatedListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Context context) {
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "Update canceled by user", Toast.LENGTH_LONG).show();
            } else if (resultCode != AppCompatActivity.RESULT_OK) {
                checkForAppUpdate();
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar.make(
                parentActivity.findViewById(R.id.postsFrameLayout),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE
        ).setAction(
                "RESTART", view -> {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                }
        ).show();
    }

    public void onResume() {
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(info -> {
                if (info.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteUpdate();
                }
            });
        }
    }

    public void onDestroy() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(stateUpdatedListener);
        }
    }
}
