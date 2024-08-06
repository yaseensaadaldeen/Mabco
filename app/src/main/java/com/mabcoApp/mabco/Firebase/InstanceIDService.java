package com.mabcoApp.mabco.Firebase;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mabcoApp.mabco.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class InstanceIDService extends FirebaseMessagingService {


    public static String returnMeFCMtoken(Context context) {

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
                        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String msg = context.getString(R.string.msg_token_fmt) + token[0];
                        Log.d(TAG, msg);
                    }
                });
        return token[0];
    }
}
