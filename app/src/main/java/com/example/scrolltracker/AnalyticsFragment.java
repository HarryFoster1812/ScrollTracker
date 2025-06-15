package com.example.scrolltracker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsFragment extends Fragment {

    private ViewPager2 viewPagerCharts;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewApps;

    private ScrollTracker tracker;

    public AnalyticsFragment(ScrollTracker tracker){
        this.tracker = tracker;
    }

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
                String selectedTime = tab.getText().toString();
                Map<String, ScrollData> data =  getScrollData(selectedTime);
                drawGraph(data, selectedTime);
                setAppInfo(data);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        ((Button)view.findViewById(R.id.getInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tracker.logScrollData();
            }
        });

        return view;
    }

    private Map<String, ScrollData> getScrollData(String mode){
        // get today date
        // depending on the mode calcualte the correct range
        // query the scrollTracker
        // clean / add values
        return null;
    }

    void drawGraph(Map<String, ScrollData> data, String mode){
        // based on the mode parse the data into week/ day collectives
        // put the data into a format the grpah can use
        // display
    }

    void setAppInfo(Map<String, ScrollData> data){
        // clear recycle viewer
        // convert into a list of packages and distances
        // sort by distance
        // convert packages into app names
        // get app icons
        // add each entry into the recycle viewer
    }

}