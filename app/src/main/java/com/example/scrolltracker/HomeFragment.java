package com.example.scrolltracker;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import java.time.LocalDate;

public class HomeFragment extends Fragment {

    private TextView distanceTextView;
    private ScrollTracker tracker;

    private static final double WALKING_SPEED_MPS = 1.42;
    private static final double CAR_LENGTH_METERS = 4.7;
    private static final double TENNIS_LENGTH_METERS = 23.77;
    private static final double OLYMPIC_SWIMMING_POOL_LENGTH_METERS = 50;

    private MaterialCardView cardCarLength;
    private MaterialCardView cardTennis;
    private MaterialCardView cardSwimming;
    private MaterialCardView cardWalking;

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
            //updateUIDistance(distance);
            animateDistanceUpdate(distance);
        }

        Button visualComparison = view.findViewById(R.id.btnVisualComparison);
        visualComparison.setOnClickListener(v -> {
            LinearLayout layout = view.findViewById(R.id.scrollEquivalents);
            if(visualComparison.getText().equals("Show Visual Comparisons")){
                fadeIn(layout);
                visualComparison.setText("Hide Visual Comparisons");
                updateVisualComparisons();

            } else {
                layout.setVisibility(View.GONE);
                fadeOut(layout);
                visualComparison.setText("Show Visual Comparisons");
            }
        });

        cardCarLength = view.findViewById(R.id.cardCarLength);
        cardTennis = view.findViewById(R.id.cardTennisCourt);
        cardSwimming = view.findViewById(R.id.cardSwimmingPool);
        cardWalking = view.findViewById(R.id.cardWalkingTime);

        setIcons();

        return view;
    }

    public void setIcons(){
        ((ImageView)cardWalking.findViewById(R.id.iconEquivalent)).setImageResource(R.drawable.ic_walk);
        ((ImageView)cardCarLength.findViewById(R.id.iconEquivalent)).setImageResource(R.drawable.ic_car);
        ((ImageView)cardSwimming.findViewById(R.id.iconEquivalent)).setImageResource(R.drawable.ic_swimming);
        ((ImageView)cardTennis.findViewById(R.id.iconEquivalent)).setImageResource(R.drawable.ic_tennis);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateVisualComparisons() {
        // Calculate the visual comparisons based on the distance
        double distance = tracker.getTotalDistance(LocalDate.now());
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

        TextView tvCarLength = cardCarLength.findViewById(R.id.tvEquivalentText);
        tvCarLength.setText(carLength + " Car Lengths");

        TextView tvTennis = cardTennis.findViewById(R.id.tvEquivalentText);
        tvTennis.setText(tennisLength + " Tennis Courts");

        TextView tvSwimming = cardSwimming.findViewById(R.id.tvEquivalentText);
        tvSwimming.setText(olympicSwimmingPoolLength + " Olympic Swimming Pools");

        TextView tvWalking = cardWalking.findViewById(R.id.tvEquivalentText);
        tvWalking.setText("The distance covered when walking for " + time_string);

    }

    // Method to update the TextView with the new distance value
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUIDistance(double distance) {
        // Update the TextView with the new distance value
        if (distanceTextView != null) {
            double TotalDistance = tracker.getTotalDistance(LocalDate.now());
            distanceTextView.setText(String.format("%.2f cm", (float) TotalDistance));

        }

        else{
            distanceTextView.setText(String.format("%.2f cm", (float)distance));

        }

    }

    private void fadeIn(View view) {
        view.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(400);
        view.startAnimation(fadeIn);
    }

    private void fadeOut(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(300);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationEnd(Animation animation) { view.setVisibility(View.GONE); }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        });
        view.startAnimation(fadeOut);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void animateDistanceUpdate(double newDistance) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) newDistance);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation ->
                distanceTextView.setText(String.format("%.2f cm", (float) animation.getAnimatedValue())));
        animator.start();
    }

}
