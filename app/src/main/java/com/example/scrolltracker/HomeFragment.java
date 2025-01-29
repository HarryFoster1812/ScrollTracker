package com.example.scrolltracker;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;

public class HomeFragment extends Fragment {

    private TextView distanceTextView;
    private ScrollTracker tracker;

    private static final double WALKING_SPEED_MPS = 1.42;
    private static final double CAR_LENGTH_METERS = 4.7;
    private static final double TENNIS_LENGTH_METERS = 23.77;
    private static final double OLYMPIC_SWIMMING_POOL_LENGTH_METERS = 50;

    public HomeFragment(ScrollTracker tracker) {
        this.tracker = tracker;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        distanceTextView = view.findViewById(R.id.tvScrollValue);
        if (tracker != null) {
            double distance = tracker.getTotalDistance(LocalDate.now());
            updateUIDistance(distance);
        }

        Button visualComparison = view.findViewById(R.id.btnVisualComparison);
        visualComparison.setOnClickListener(v -> {
            ConstraintLayout layout = view.findViewById(R.id.scrollEquivalents);
            if(visualComparison.getText().equals("Show Visual Comparisons")){
                layout.setVisibility(View.VISIBLE);
                visualComparison.setText("Hide Visual Comparisons");
            } else {
                layout.setVisibility(View.GONE);
                visualComparison.setText("Show Visual Comparisons");
            }
        });

        return view;
    }

    public void updateVisualComparisons(double distance) {
        // Calculate the visual comparisons based on the distance
        double distanceMeters = distance / 100;
        double carLength = distanceMeters / CAR_LENGTH_METERS;
        double tennisLength = distanceMeters / TENNIS_LENGTH_METERS;
        double olympicSwimmingPoolLength = distanceMeters / OLYMPIC_SWIMMING_POOL_LENGTH_METERS;

        double walkingTimeSeconds = distanceMeters / WALKING_SPEED_MPS;
        double walkingTimeMinutes = walkingTimeSeconds/60;
        double walkingTimeHours = walkingTimeMinutes/60;

        String time_string= "";

        if (walkingTimeHours < 1){
            // set text to walking time in minutes
            time_string = String.format("%d minutes", (int)Math.floor(walkingTimeMinutes));
        }
        else{
            // set text to x hours and y minutes
            walkingTimeMinutes = walkingTimeMinutes - (walkingTimeHours * 60);
            time_string = String.format("%d hours and %d minutes", (int)Math.floor(walkingTimeHours), (int)Math.floor(walkingTimeMinutes));


        }

        // Set all of the text views to the new comparisons


    }

    // Method to update the TextView with the new distance value
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUIDistance(double distance) {
        // Update the TextView with the new distance value
        if (distanceTextView != null) {
            distanceTextView.setText(String.format("%.2f cm", (float) tracker.getTotalDistance(LocalDate.now())));
        }
    }
}
