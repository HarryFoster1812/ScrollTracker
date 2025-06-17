package com.ScrollTracker.scrolltracker;

import static android.view.View.INVISIBLE;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ScrollTracker.scrolltracker.FragmentControllers.Analytics.AnalyticsFragment;
import com.ScrollTracker.scrolltracker.FragmentControllers.HomeFragment;
import com.ScrollTracker.scrolltracker.FragmentControllers.Settings.SettingsFragment;
import com.ScrollTracker.scrolltracker.FragmentControllers.WelcomeFragment.WelcomeFragment;
import com.ScrollTracker.scrolltracker.ScrollService.ScrollAccessibilityService;
import com.ScrollTracker.scrolltracker.ScrollService.ScrollTracker;
import com.example.scrolltracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    String PACKAGE_NAME;
    ScrollTracker tracker;
    public BottomNavigationView bottom_nav;
    public HomeFragment homeFragment;
    Fragment currentFragment;

    // Receiver for receiving distance updates from ScrollAccessibilityService
    private final BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "com.example.scrolltracker.DISTANCE_UPDATED".equals(intent.getAction())) {
                double distance = intent.getDoubleExtra("distance", 0f);
                String action_package_name = intent.getStringExtra("package");

                // Update the UI with the new distance
                if (Objects.equals(action_package_name, PACKAGE_NAME)) {
                    updateHomeFragmentUI(distance);
                }

                tracker.addDistance(action_package_name, distance);
            }
        }
    };

    /*

*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String theme = sharedPreferences.getString("theme", "light");

        switch (theme) {
            case "light": setTheme(R.style.Theme_Light); break;
            case "dark": setTheme(R.style.Theme_Dark); break;
            case "blue": setTheme(R.style.Theme_Blue); break;
            case "green": setTheme(R.style.Theme_Green); break;
        }

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        // Initialise Package Name
        PACKAGE_NAME = getApplicationContext().getPackageName();

        // Initialise ScrollTracker

        tracker = new ScrollTracker(this.getApplicationContext());
         bottom_nav = findViewById(R.id.bottomNavigation);

        // check permissions
        boolean allPermissionsGranted = checkAllPermissions();
        homeFragment = new HomeFragment(tracker);

        if(allPermissionsGranted) {
            loadFragment(homeFragment);
        }
        else{
            // load welcome fragment
            loadFragment(new WelcomeFragment(this));
            bottom_nav.setVisibility(INVISIBLE);
        }

        bottom_nav.setOnItemSelectedListener(item -> {
            Fragment selected_fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selected_fragment = homeFragment;
            } else if (id == R.id.nav_analytics) {
                selected_fragment = new AnalyticsFragment(this.tracker);

            } else if (id == R.id.nav_settings) {
                selected_fragment = new SettingsFragment();

            }
            loadFragment(selected_fragment);
            return true;
        });

        // Register the receiver dynamically to listen for the distance updates
        IntentFilter filter = new IntentFilter("com.example.scrolltracker.DISTANCE_UPDATED");
        registerReceiver(distanceReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver dynamically to listen for the distance updates
        if (tracker != null) {
            double distance = tracker.getTotalDistance(LocalDate.now());
            updateHomeFragmentUI(distance);
        }

        boolean isEnabled = isAccessibilityServiceEnabled(getApplicationContext(), ScrollAccessibilityService.class);
        if (!isEnabled && !(currentFragment instanceof WelcomeFragment)) {
            showAccessibilitySnackbar();
        }

    }

    public boolean checkAllPermissions(){
        boolean accessibilityEnabled = isAccessibilityServiceEnabled(getApplicationContext(), ScrollAccessibilityService.class);
        boolean notificationEnabled = isNotificationsEnabled(getApplicationContext());
        boolean isBatteryNotOptimised = isBatteryOptimizationIgnored(getApplicationContext());
        return accessibilityEnabled && notificationEnabled && isBatteryNotOptimised;
    }

    private void updateHomeFragmentUI(double distance){
        if (homeFragment != null && currentFragment == homeFragment) {
            homeFragment.updateUIDistance(distance);
            homeFragment.updateVisualComparisons();
        }
    }

    private void showAccessibilitySnackbar() {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "It looks like you have not enabled this app as an Accessibility Service",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Enable", v -> openAccessibilitySettings());
        snackbar.show();
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(service.getName()))
                return true;
        }

        return false;
    }

    public boolean isBatteryOptimizationIgnored(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm != null && pm.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public boolean isNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            return manager.areNotificationsEnabled();
        }
    }



    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(distanceReceiver);
    }
}


/*
NOTES:

Length visualisation:
Car length: 4.7m
Tennis court: (~23.77 meters long)
Olympic Swimming Pool: 50m
American Football Field: 91.44 m
Football Field: 105m

Time visualisation:
How long to walk the distance

 */