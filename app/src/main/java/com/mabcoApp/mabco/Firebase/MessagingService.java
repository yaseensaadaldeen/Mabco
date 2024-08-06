package com.mabcoApp.mabco.Firebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "my_channel_id";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // Handle the received notification
      String Body=  message.getData().get("body").toString();
         String title=      message.getData().get("title").toString();
        showNotification(title, Body);
    }

    private void showNotification(String title, String message) {
        createNotificationChannel();

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent cancelIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent openIntent = new Intent(this, MainActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(false) // Notification cannot be swiped away
                .setOngoing(true) // Make it ongoing
                .addAction(R.drawable.remove_vector, "Cancel", cancelIntent) // Add cancel button
                .addAction(R.drawable.error_outline_24, "Open", openPendingIntent); // Add cancel button
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel for Firebase notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}