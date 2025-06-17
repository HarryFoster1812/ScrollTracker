package com.ScrollTracker.scrolltracker.FragmentControllers.WelcomeFragment;

import static com.fasterxml.jackson.databind.util.ClassUtil.getPackageName;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ScrollTracker.scrolltracker.FragmentControllers.WelcomeFragment.WelcomeFragment;
import com.example.scrolltracker.R;
import com.google.android.material.button.MaterialButton;

public class PermissionPageFragment extends Fragment {
    private static final String ARG_POSITION = "position";

    public static PermissionPageFragment newInstance(int position) {
        PermissionPageFragment fragment = new PermissionPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.permission_item, container, false);
        int position = getArguments() != null ? getArguments().getInt(ARG_POSITION) : 0;
        configurePage(view, position);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configurePage(View view, int position) {
        ImageView icon = view.findViewById(R.id.permissionIcon);
        TextView title = view.findViewById(R.id.permissionTitle);
        TextView description = view.findViewById(R.id.permissionDescription);
        ImageView statusIcon = view.findViewById(R.id.permissionStatusIcon);
        MaterialButton cta = view.findViewById(R.id.permissionCta);
        MaterialButton continueButton = view.findViewById(R.id.continueButton);

        switch (position) {
            case 0: // Notifications
                icon.setImageResource(R.drawable.baseline_notifications);
                title.setText("Step 1: Notifications");
                description.setText("Required to show that ScrollTracker is actively running in the background.");
                cta.setOnClickListener(v ->{
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
                    } else {
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                    }
                    startActivity(intent);
                });
                break;
            case 1: // Battery Optimization
                icon.setImageResource(R.drawable.ic_battery_alert);
                title.setText("Step 2: Battery Optimization");
                description.setText("Needed to keep ScrollTracker running in the background reliably.");
                cta.setOnClickListener(v -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                    startActivity(intent);
                });
                break;
            case 2: // Accessibility
                icon.setImageResource(R.drawable.ic_accessibility);
                title.setText("Step 3: Accessibility Service");
                description.setText("Used to detect scrolling across apps. Required for tracking how much you scroll.");
                cta.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
                continueButton.setVisibility(View.VISIBLE);
                continueButton.setOnClickListener(v -> {
                    // Navigate to next screen or complete setup
                });
                break;
        }

        updatePermissionState(statusIcon, cta, continueButton, position);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updatePermissionState(ImageView statusIcon, MaterialButton cta, MaterialButton continueButton, int position) {
        Context context = requireContext();
        boolean isEnabled = false;
        switch (position) {
            case 0: // Notifications
                isEnabled = NotificationManagerCompat.getEnabledListenerPackages(context)
                        .contains(context.getPackageName());
                break;
            case 1: // Battery Optimization
                PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                isEnabled = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
                break;
            case 2: // Accessibility
                AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
                isEnabled = accessibilityManager.isEnabled();
                break;
        }

        statusIcon.setImageResource(isEnabled ? R.drawable.ic_check : R.drawable.ic_warning);
        statusIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,
                isEnabled ? R.color.status_enabled : R.color.status_disabled)));
        cta.setText(isEnabled ? "Enabled" : "Enable Now");
        cta.setEnabled(!isEnabled);
        if (position == 2) {
            continueButton.setEnabled(isEnabled);
        }

        // Notify parent fragment to advance to next page if permission is granted
        if (isEnabled && position < 2) {
            if (getParentFragment() instanceof WelcomeFragment) {
                ((WelcomeFragment) getParentFragment()).onPermissionGranted(position);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        // Refresh permission state when returning from settings
        View view = getView();
        if (view != null) {
            int position = getArguments() != null ? getArguments().getInt(ARG_POSITION) : 0;
            updatePermissionState(
                    view.findViewById(R.id.permissionStatusIcon),
                    view.findViewById(R.id.permissionCta),
                    view.findViewById(R.id.continueButton),
                    position
            );
        }
    }
}