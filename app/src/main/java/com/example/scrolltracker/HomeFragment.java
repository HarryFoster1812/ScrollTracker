package com.example.scrolltracker;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;

public class HomeFragment extends Fragment {

    private TextView distanceTextView;
    private ScrollTracker tracker;

    public HomeFragment(ScrollTracker tracker) {
        this.tracker = tracker;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        distanceTextView = view.findViewById(R.id.tvScrollValue);

        return view;
    }

    // Method to update the TextView with the new distance value
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUIDistance(double distance) {
        // Update the TextView with the new distance value
        distanceTextView.setText(String.format("%.2f cm", (float)tracker.getTotalDistance(LocalDate.now())));

    }
}
