package com.mabcoApp.mabco.Classes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewException;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class InAppReview {

    public void askUserForReview(Activity activity) {
        ReviewManager reviewManager = ReviewManagerFactory.create(activity);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getBoolean("is_reviewed", false)) {
            request.addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {  // Use task here instead of request
                        ReviewInfo reviewInfo = task.getResult();
                        Task<Void> reviewFlow = reviewManager.launchReviewFlow(activity, reviewInfo);

                        reviewFlow.addOnCompleteListener(task1 -> {
                            settings.edit().putBoolean("is_reviewed", true).commit();
                            Toast.makeText(activity, "Review flow completed.", Toast.LENGTH_SHORT).show();

                            // Review flow completed, handle as needed
                        }).addOnFailureListener(error1 -> {
                            Toast.makeText(activity, "Failed to launch review flow", Toast.LENGTH_SHORT).show();
                            openAppInPlayStore(activity);
                        });
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ReviewException) {
                            String reviewError = ((ReviewException) exception).getMessage();
                            Toast.makeText(activity, "Failure: " + reviewError, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "Unknown error occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(activity, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(error -> {
                Toast.makeText(activity, "Request failed", Toast.LENGTH_SHORT).show();
                openAppInPlayStore(activity);
            });
        } else {
            openAppInPlayStore(activity);
        }
    }

    public void openAppInPlayStore(Activity activity) {
        final String appPackageName = "com.mabcoApp.application.mabco"; // Replace with your actual app package name

        try {
            // Try to open the app page in the Google Play app
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // If Google Play is not installed, open in browser
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
