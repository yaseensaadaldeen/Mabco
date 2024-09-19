package com.mabcoApp.mabco.Firebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.mabcoApp.mabco.Classes.Offer;
import com.mabcoApp.mabco.Classes.Product;
import com.mabcoApp.mabco.MainActivity;
import com.mabcoApp.mabco.R;
import com.mabcoApp.mabco.UrlEndPoint;

import java.util.HashMap;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "my_channel_id";
    public String type = "", title = "", body = "", productJson = "",Notif_id = "";
    public Product product;
    public Offer offer;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // Handle the received notification
        Map<String, String> data = message.getData();
        if (data.containsKey("Type")) type = data.get("Type");

        if (data.containsKey("Type") && "product".equals(data.get("Type"))) {
            productJson = data.get("notification_data");
            Gson gson = new Gson();
            product = gson.fromJson(productJson, Product.class);
        } else if (data.containsKey("Type") && "offer".equals(data.get("Type"))) {
            Gson gson = new Gson();
            String offerDataJson = data.get("notification_data");
            offer = gson.fromJson(offerDataJson, Offer.class);
        }
        title = data.get("title") != null ? data.get("title") : "Default Title";
        body = data.get("body") != null ? data.get("body") : "Default Body";
        Notif_id = data.get("Notif_ID") != null ? data.get("Notif_ID") : "0";
        if (data.get("verification") == null){
            sendNotificationAcknowledgment(Notif_id);
            showNotification(title, body,Notif_id);}
    }

    private void showNotification(String title, String message,String Notif_id) {
        createNotificationChannel();
        try {
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent cancelIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            Intent openIntent = new Intent(this, MainActivity.class);
            openIntent.putExtra("from", "notification");
            openIntent.putExtra("Type", type);
            openIntent.putExtra("Notif_id", Notif_id);
            if (type.equals("product") && product != null)
                openIntent.putExtra("Product", productJson);
            else if (type.equals("offer") && offer != null) openIntent.putExtra("Offer", offer);

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    // Method to send API call using Volley
    private void sendNotificationAcknowledgment(String Notif_id) {
        SharedPreferences Token = this.getSharedPreferences("Token", Context.MODE_PRIVATE);
        String UserID = Token.getString("UserID", "");

        // Define the URL for your API endpoint
        String url = UrlEndPoint.General + "service1.svc/insertAPPNotificationLog/"+UserID+","+Notif_id;

        // Create a JSONObject to send in the request body

        // Create a Volley request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        com.mabcoApp.mabco.HttpsTrustManager.allowAllSSL();
        StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response
                        Log.d("Volley", "Notification acknowledgment sent: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("Volley", "Error sending acknowledgment: " + error.getMessage());
                    }
                }
        ){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-Content-Type-Options", "nosniff");
                params.put("X-XSS-Protection", "0");
                params.put("X-Frame-Options", "DENY");
                //..add other headers
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

}