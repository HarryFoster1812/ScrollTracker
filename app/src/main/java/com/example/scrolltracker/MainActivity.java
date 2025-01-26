package com.example.scrolltracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private TextView distanceTextView;
    String PACKAGE_NAME;
    ScrollTracker tracker;

    // Receiver for receiving distance updates from ScrollAccessibilityService
    private final BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Broadcast Recieved!");
            if (intent != null && "com.example.scrolltracker.DISTANCE_UPDATED".equals(intent.getAction())) {
                float distance = intent.getFloatExtra("distance", 0f);
                String action_package_name = intent.getStringExtra("package");

                // Update the UI with the new distance
                if (Objects.equals(action_package_name, PACKAGE_NAME)) {
                    updateUIDistance(distance);
                }

                tracker.addDistance(action_package_name, distance);
            }
        }
    };

    // Method to update the TextView with the new distance value
    private void updateUIDistance(float distance) {
        if (distanceTextView != null) {
            runOnUiThread(() -> {
                // Update the TextView with the new distance value
                distanceTextView.setText(String.format("%.2f cm", distance));
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView for displaying distance
        distanceTextView = findViewById(R.id.tvScrollValue);

        // Initialise Package Name
        PACKAGE_NAME = getApplicationContext().getPackageName();

        // Initialise ScrollTracker

        tracker = new ScrollTracker(this.getApplicationContext());

        // Register the receiver dynamically to listen for the distance updates
        IntentFilter filter = new IntentFilter("com.example.scrolltracker.DISTANCE_UPDATED");
        registerReceiver(distanceReceiver, filter, Context.RECEIVER_EXPORTED);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver dynamically to listen for the distance updates
        if (tracker != null) {
            distanceTextView.setText(String.format("%.2f cm", tracker.getTotalDistance(LocalDate.now())));
        }
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