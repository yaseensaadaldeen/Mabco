package com.mabcoApp.mabco;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mabcoApp.mabco.Classes.InAppReview;
import com.mabcoApp.mabco.Firebase.InstanceIDService;
import com.mabcoApp.mabco.databinding.ActivityMainBinding;
import com.mabcoApp.mabco.ui.Policies.PrivacyPolicyDialogStartFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private static final int RC_NOTIFICATION = 99;
    TextView notification_cnt;
    String vir;
    String version;
    String link;
    TextView userNameTextView;
    RequestQueue requestQueue;
    NavigationView navigationView;
    private InAppReview inAppReview = new InAppReview();
    private DrawerLayout drawer;
    private static final int REQUEST_WRITE_STORAGE = 112;
    SharedPreferences ShoppingCartData, UserData, PersonalPreference, compareSharedPreferences;

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
            compareSharedPreferences = this.getSharedPreferences("ProductsCompare", Context.MODE_PRIVATE);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setSupportActionBar(binding.appBarMain.toolbar);
            InstanceIDService.returnMeFCMtoken(this);

            drawer = binding.drawerLayout;
            navigationView = binding.navView;
            View headerView = navigationView.getHeaderView(0);
            userNameTextView = headerView.findViewById(R.id.sidebar_user_name);
            userNameTextView.setText(UserData.getString("user_name", ""));
            mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_share, R.id.nav_services, R.id.productsFragment, R.id.webview, R.id.nav_compare).setOpenableLayout(drawer).build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);

            // Set item selected listener
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                boolean handled = false;
                if (id == R.id.nav_Rate) {
                    // Trigger the in-app review flow
                    inAppReview.askUserForReview(this);
                    handled = true;
                } else if (id == R.id.nav_share) {
                    // Trigger the share app flow
                    shareApp(this);
                    handled = true;
                } else {
                    // Handle other items by navigating
                    handled = NavigationUI.onNavDestinationSelected(item, navController);
                }

                // Close drawer after action
                drawer.closeDrawers();
                return handled;
            });

            BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.productsFragment, R.id.profileFragment, R.id.navigation_showrooms).build();
            NavController bottomnavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupWithNavController(navView, bottomnavController);
            checkPermissions();
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (Exception e) {
                e.printStackTrace();
                version = "";
            }

            int verCode = pInfo.versionCode;
            checkUpdate(verCode);

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
            FirebaseMessaging.getInstance().subscribeToTopic(com.mabcoApp.mabco.UrlEndPoint.notification_topic);
        } else {
            // User denied permission, unsubscribe from the topic
            FirebaseMessaging.getInstance().unsubscribeFromTopic(com.mabcoApp.mabco.UrlEndPoint.notification_topic);
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
            final String PREFS_NAME = "MyPrefsFile";

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

            if (settings.getBoolean("my_first_time", true)) {
                Log.d("Comments", "First time");
                BottomSheetDialogFragment bottomSheetDialogFragment = new PrivacyPolicyDialogStartFragment();
                bottomSheetDialogFragment.setCancelable(false);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
                settings.edit().putBoolean("my_first_time", false).commit();
            }
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

    com.mabcoApp.mabco.BadgeDrawerArrowDrawable badgeDrawerArrowDrawable;
    ActionBarDrawerToggle mDrawerToggle;

    private void setupDrawerLayout() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setBadgeNavigationIcon() {
        if (getSupportActionBar() != null) {
            Context themedContext = getSupportActionBar().getThemedContext();
            if (themedContext != null) {
                if (badgeDrawerArrowDrawable == null) {
                    badgeDrawerArrowDrawable = new com.mabcoApp.mabco.BadgeDrawerArrowDrawable(themedContext);
                }
                mDrawerToggle.setDrawerArrowDrawable(badgeDrawerArrowDrawable);
                badgeDrawerArrowDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.holo_red_light));
            }
        }
    }

    //app slide notification
    public void AddToolbarNotification(int count, int nav_item, boolean notify) {
        setupDrawerLayout();
        setBadgeNavigationIcon();
        badgeDrawerArrowDrawable.setEnabled(notify);
        badgeDrawerArrowDrawable.setText(null);

        RelativeLayout customLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.notification_badge, null);
        TextView badge = (customLayout.findViewById(R.id.counter));
        badge.setText(count > 0 ? String.valueOf(count) : "");
        badge.setVisibility(notify ? View.VISIBLE : View.GONE);
        navigationView.getMenu().findItem(nav_item).setActionView(customLayout);

    }

    public void openAppInPlayStore() {
        //final String appPackageName = getPackageName(); // Get the current app package name
        final String appPackageName = "com.mabcoApp.application.mabco"; // Get the current app package name

        try {
            // Try to open the app page in Google Play app
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            // If Google Play is not installed, open in browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void shareApp(Context context) {
        String appPackageName = context.getPackageName(); // Get the package name of the app
        String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName; // Create the link

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this great app: " + appLink);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share App via:");
        context.startActivity(shareIntent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void checkUpdate(int verCode) {
        HttpsTrustManager.allowAllSSL();
        String url2 = UrlEndPoint.General + "service1.svc/checkforupdateswithlink/";
        StringRequest strRequest1 = new StringRequest(Request.Method.GET, url2 + verCode + "," + "1", new Response.Listener<String>() {
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray stat = jsonResponse.getJSONArray("checkforupdateswithlinkResult");

                    //if(!jsonResponse.isNull("ord_no"))
                    //  for(int i = 0; i < stat.length(); i++)
                    {
                        link = stat.getJSONObject(0).getString("mobile_slno");

                        vir = stat.getJSONObject(0).getString("ord_no");
                    }

                    if (!vir.equals("true")) {


                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);

                        // Setting Dialog Title
                        alertDialog.setTitle(R.string.update_app);

                        // Setting Dialog Message
                        alertDialog.setMessage(R.string.update_desc);

                        // Setting Icon to Dialog
                        //alertDialog.setIcon(R.drawable.);

                        // Setting Positive "Yes" Button
                        alertDialog.setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                startActivity(browserIntent);
                            }
                        });

                        // Setting Negative "NO" Button
                        alertDialog.setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to invoke NO event

                            }
                        });
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(strRequest1);
    }
}