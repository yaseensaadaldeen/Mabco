package com.example.mabco;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mabco.Firebase.InstanceIDService;
import com.example.mabco.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int RC_NOTIFICATION = 99;
    TextView notification_cnt;
    TextView userNameTextView;
    private static final int REQUEST_WRITE_STORAGE = 112;
    SharedPreferences ShoppingCartData, UserData, PersonalPreference;


    public void updateUserName(String newUserName) {
        userNameTextView.setText(newUserName);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ShoppingCartData = this.getSharedPreferences("ShoppingCartData", Context.MODE_PRIVATE);
            UserData = this.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            PersonalPreference = this.getSharedPreferences("PersonalData", Context.MODE_PRIVATE);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.appBarMain.toolbar);
            InstanceIDService.returnMeFCMtoken(this);

            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;
            View headerView = navigationView.getHeaderView(0);
            userNameTextView = headerView.findViewById(R.id.sidebar_user_name);
            userNameTextView.setText(UserData.getString("user_name", ""));
            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_share, R.id.nav_services, R.id.productsFragment,R.id.offersFragment).setOpenableLayout(drawer).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);


            BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.productsFragment, R.id.profileFragment, R.id.navigation_showrooms).build();
            NavController bottomnavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupWithNavController(navView, bottomnavController);
//            if (PersonalPreference.getString("NotificationStatus","notset") .equals( "notset")) {
//                //requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, RC_NOTIFICATION);
//            }
            checkPermissions();
            Bundle b = getIntent().getExtras();
            if (b != null && getIntent().getExtras().get("Type") != null) {

            }
        } catch (Exception ex) {
            Log.i("brandAdapter exception", ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void checkPermissions() {
        Dexter.withContext(this).withPermissions(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    private void proceedWithFileOperations(boolean granted) {
        SharedPreferences.Editor editor = PersonalPreference.edit();
        editor.putString("WriteStorageStatus", granted ? "enable" : "disable");
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_NOTIFICATION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted by the user
                // You can call your notification-related code here
                handleNotificationPermission(true);
            } else {
                // Permission denied by the user
                handleNotificationPermission(false);
            }
        }
        if (requestCode == REQUEST_WRITE_STORAGE) {
            // Permission granted
            proceedWithFileOperations(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
        if (requestCode == RC_NOTIFICATION) {
            // Permission granted
            handleNotificationPermission(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }

    }

    private void handleNotificationPermission(boolean granted) {
        SharedPreferences.Editor editor = PersonalPreference.edit();
        editor.putString("NotificationStatus", granted ? "enable" : "disable");
        editor.apply();

        if (granted) {
            // User granted permission, subscribe to the topic
            FirebaseMessaging.getInstance().subscribeToTopic(UrlEndPoint.notification_topic);
        } else {
            // User denied permission, unsubscribe from the topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic(UrlEndPoint.notification_topic);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.shopping_cart_item, menu);
            MenuItem item = menu.findItem(R.id.action_shopping_cart);
            MenuItemCompat.setActionView(item, R.layout.shopping_cart_icon_layout);
            RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
            TextView notification_cnt = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
            String text = String.valueOf(ShoppingCartData.getInt("items_count", 0));
            notification_cnt.setText(text);
            if (notification_cnt.getText().equals("0")) notification_cnt.setVisibility(View.GONE);
            ImageView shoppingcarticon = (ImageView) notifCount.findViewById(R.id.shopping_icon_topbar);
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            notifCount.setOnClickListener(v -> {
                int destinationIdToNavigate = R.id.shoppingCartFragment;
                // Check if the current destination is the same as the destination to navigate
                if (navController.getCurrentDestination() == null || navController.getCurrentDestination().getId() != destinationIdToNavigate) {
                    navController.navigate(destinationIdToNavigate);
                }
            });
            shoppingcarticon.setOnClickListener(v -> {
                int destinationIdToNavigate = R.id.shoppingCartFragment;
                // Check if the current destination is the same as the destination to navigate
                if (navController.getCurrentDestination() == null || navController.getCurrentDestination().getId() != destinationIdToNavigate) {
                    navController.navigate(destinationIdToNavigate);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void updateNotificationCount(int count) {
        // Find the notification TextView and update its text
        try {
            notification_cnt = findViewById(R.id.actionbar_notifcation_textview);
            notification_cnt.setText(String.valueOf(count));
            if (notification_cnt.getText().equals("0")) notification_cnt.setVisibility(View.GONE);
            else notification_cnt.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        switch (item.getItemId()) {
            case R.id.action_shopping_cart:
                navController.navigate(R.id.shoppingCartFragment);
                return true;
            default:
                return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

}