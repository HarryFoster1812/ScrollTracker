package com.example.scrolltracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private ViewPager2 viewPagerCharts;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewApps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        // Initialize UI elements
        viewPagerCharts = view.findViewById(R.id.viewPagerCharts);
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerViewApps = view.findViewById(R.id.recyclerViewApps);

        // Setup tabs (Day, Week, Month)
        tabLayout.addTab(tabLayout.newTab().setText("Day"));
        tabLayout.addTab(tabLayout.newTab().setText("Week"));
        tabLayout.addTab(tabLayout.newTab().setText("Month"));

        // Sync ViewPager with Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerCharts.setCurrentItem(tab.getPosition());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }


}