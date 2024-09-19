package com.mabcoApp.mabco.Firebase;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;

import java.io.UnsupportedEncodingException;


public class InstanceIDService extends FirebaseMessagingService {
    public static MainActivity mainActivity;

    public InstanceIDService(MainActivity activity) {
        this.mainActivity = activity;
    }


    public static String returnMeFCMtoken(Context context) {
        SharedPreferences Token = context.getSharedPreferences("Token", Context.MODE_PRIVATE);

        final String[] token = {""};
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token[0] = task.getResult();
                        String stored_token = Token.getString("Token", "");
                        if (stored_token.equals("") || !stored_token.equals(token[0])) {
                            try {
                                mainActivity.AppDataAnalytics("0", token[0]);
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String msg = context.getString(R.string.msg_token_fmt) + token[0];
                        Log.d(TAG, msg);
                    }
                });
        return token[0];
    }
}
