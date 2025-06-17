package com.ScrollTracker.scrolltracker.FragmentControllers.WelcomeFragment;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ScrollTracker.scrolltracker.FragmentControllers.Settings.CreditsFragment;
import com.ScrollTracker.scrolltracker.MainActivity;
import com.ScrollTracker.scrolltracker.ScrollService.ScrollAccessibilityService;
import com.example.scrolltracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class WelcomeFragment extends Fragment {

    MainActivity activity;

    public WelcomeFragment(MainActivity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.permissionPager);
        TabLayout tabLayout = view.findViewById(R.id.tabIndicator);

        // Set up ViewPager2
        viewPager.setAdapter(new PermissionAdapter(this));
        viewPager.setUserInputEnabled(false); // Disable manual swiping

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {}).attach();
        tabLayout.getTabAt(0).setText("1");
        tabLayout.getTabAt(1).setText("2");
        tabLayout.getTabAt(2).setText("3");

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            tabView.setOnTouchListener((v, event) -> true); // Consume all touch events
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void switchToHome(){
        activity.bottom_nav.setVisibility(VISIBLE);
        activity.loadFragment(activity.homeFragment);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onPermissionGranted(int position) {
        ViewPager2 viewPager = requireView().findViewById(R.id.permissionPager);
        if (position < 2 && isPermissionEnabled(position)) {
            viewPager.setCurrentItem(position + 1, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isPermissionEnabled(int position) {
        Context context = requireContext();
        switch (position) {
            case 0: // Notifications
                return activity.isNotificationsEnabled(context);
            case 1: // Battery Optimization
                return activity.isBatteryOptimizationIgnored(context);
            case 2: // Accessibility
                return MainActivity.isAccessibilityServiceEnabled(context, ScrollAccessibilityService.class);
            default:
                return false;
        }
    }

}

@RequiresApi(api = Build.VERSION_CODES.O)
class PermissionAdapter extends FragmentStateAdapter {
    public PermissionAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        return PermissionPageFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
