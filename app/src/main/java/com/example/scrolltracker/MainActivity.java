package com.example.scrolltracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView distanceTextView;

    // Receiver for receiving distance updates from ScrollAccessibilityService

    private final BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Broadcast Recieved!");
            if (intent != null && "com.example.scrolltracker.DISTANCE_UPDATED".equals(intent.getAction())) {
                float distance = intent.getFloatExtra("distance", 0f);
                // Update the UI with the new distance
                updateDistance(distance);
            }
        }
    };


    // Method to update the TextView with the new distance value
    private void updateDistance(float distance) {
        if (distanceTextView != null) {
            runOnUiThread(() -> {
                // Update the TextView with the new distance value
                distanceTextView.setText(String.format("%.2f cm", distance));
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the TextView for displaying distance
        distanceTextView = findViewById(R.id.distanceTextView);

        // Register the receiver dynamically to listen for the distance updates
        IntentFilter filter = new IntentFilter("com.example.scrolltracker.DISTANCE_UPDATED");
        registerReceiver(distanceReceiver, filter, Context.RECEIVER_EXPORTED);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(distanceReceiver);
    }
}
