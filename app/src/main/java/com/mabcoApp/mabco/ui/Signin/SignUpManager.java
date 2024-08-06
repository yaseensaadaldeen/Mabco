package com.mabcoApp.mabco.ui.Signin;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


public class SignUpManager {

    private static final String PREF_NAME = "SignUpPrefs";
    private static final String KEY_ATTEMPTS = "attempts";
    private static final String KEY_LAST_ATTEMPT_TIME = "lastAttemptTime";

    private static final int MAX_ATTEMPTS = 5;
    private static final long WAITING_PERIOD_MILLIS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

    private Context context;
    private SharedPreferences sharedPreferences;

    public SignUpManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean canSignUp() {
        int attempts = sharedPreferences.getInt(KEY_ATTEMPTS, 0);
        long lastAttemptTime = sharedPreferences.getLong(KEY_LAST_ATTEMPT_TIME, 0);

        if (attempts < MAX_ATTEMPTS) {
            return true; // User can still make attempts
        } else {
            // Check if waiting period has passed
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttemptTime >= WAITING_PERIOD_MILLIS) {
                // Reset attempts and update last attempt time
                resetAttempts();
                return true;
            } else {
                return false; // User must wait for the next attempt
            }
        }
    }

    public void signUp() {
        int attempts = sharedPreferences.getInt(KEY_ATTEMPTS, 0);
        sharedPreferences.edit().putInt(KEY_ATTEMPTS, attempts + 1).apply();
        sharedPreferences.edit().putLong(KEY_LAST_ATTEMPT_TIME, System.currentTimeMillis()).apply();
        Toast.makeText(context, "SignUp Attempts remaining "+String.valueOf(attempts + 1), Toast.LENGTH_SHORT).show();
    }

    public long getRemainingTime() {
        long lastAttemptTime = sharedPreferences.getLong(KEY_LAST_ATTEMPT_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastAttemptTime;
        return Math.max(0, WAITING_PERIOD_MILLIS - elapsedTime);
    }

    public void resetAttempts() {
        sharedPreferences.edit().putInt(KEY_ATTEMPTS, 1).apply();
        sharedPreferences.edit().putLong(KEY_LAST_ATTEMPT_TIME, System.currentTimeMillis()).apply();
    }
}
