package com.example.scrolltracker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    String PACKAGE_NAME;
    ScrollTracker tracker;
    HomeFragment homeFragment;

    // Receiver for receiving distance updates from ScrollAccessibilityService
    private final BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Broadcast Recieved!");
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialise Package Name
        PACKAGE_NAME = getApplicationContext().getPackageName();

        // Initialise ScrollTracker

        tracker = new ScrollTracker(this.getApplicationContext());

        homeFragment = new HomeFragment(tracker);
        loadFragment(homeFragment, "HOME_FRAGMENT");
        BottomNavigationView bottom_nav = findViewById(R.id.bottomNavigation);
        bottom_nav.setOnItemSelectedListener(item -> {
            Fragment selected_fragment = null;
            String tag= "";
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selected_fragment = new HomeFragment(tracker);
                tag = "HOME_FRAGMENT";
            } else if (id == R.id.nav_analytics) {
                selected_fragment = new AnalyticsFragment();
                tag = "ANALYTICS_FRAGMENT";

            } else if (id == R.id.nav_settings) {
                selected_fragment = new SettingsFragment();
                tag = "SETTINGS_FRAGMENT";

            }
            loadFragment(selected_fragment, tag);
            return true;
        });

        // Register the receiver dynamically to listen for the distance updates
        IntentFilter filter = new IntentFilter("com.example.scrolltracker.DISTANCE_UPDATED");
        registerReceiver(distanceReceiver, filter, Context.RECEIVER_EXPORTED);

        boolean isEnabled = isAccessibilityServiceEnabled(getApplicationContext(), ScrollAccessibilityService.class);
        if (!isEnabled) {
            showAccessibilitySnackbar();
        }

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
        if (!isEnabled) {
            showAccessibilitySnackbar();
        }

    }

    private void updateHomeFragmentUI(double distance){
        if (homeFragment != null) {
            homeFragment.updateUIDistance(distance);
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

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment);
        transaction.commit();
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

 */