package com.ScrollTracker.scrolltracker;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.scrolltracker.R;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalyticsFragment extends Fragment {

    private ViewPager2 viewPagerCharts;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewApps;
    private AppEntryAdapter adapter;

    private ScrollTracker tracker;

    public AnalyticsFragment(ScrollTracker tracker){
        this.tracker = tracker;
    }

    @Nullable
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        // Initialize UI elements
        viewPagerCharts = view.findViewById(R.id.viewPagerCharts);
        // setup 3 charts
        // - Bar for Day
        // - Line for week (data per day)
        // - Line for month (data per week/day?)

        // create and load all charts
        // add to the viewPager

        tabLayout = view.findViewById(R.id.tabLayout);

        recyclerViewApps = view.findViewById(R.id.recyclerViewApps);
        recyclerViewApps.setLayoutManager(new LinearLayoutManager(recyclerViewApps.getContext()));
        adapter = new AppEntryAdapter(new ArrayList<>());
        recyclerViewApps.setAdapter(adapter);

        // Setup tabs (Day, Week, Month)
        tabLayout.addTab(tabLayout.newTab().setText("Day"));
        tabLayout.addTab(tabLayout.newTab().setText("Week"));
        tabLayout.addTab(tabLayout.newTab().setText("Month"));

        // Sync ViewPager with Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTab(tab);
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

        // Connect viewPager to tabs

        tabLayout.post(() -> tabLayout.post(()->handleTab(tabLayout.getTabAt(0)))); // manually handle the first tab

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    private void handleTab(TabLayout.Tab tab){
         viewPagerCharts.setCurrentItem(tab.getPosition());
         String selectedTime = tab.getText().toString();
         Set<Map.Entry<String, ScrollData>> data =  getScrollData(selectedTime);
         drawGraph(data, selectedTime);
         setAppInfo(data);
         // trigger chart to change
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Set<Map.Entry<String, ScrollData>> getScrollData(String mode){
        // get today date
        String dateToday = LocalDate.now().toString();
        Set<Map.Entry<String, ScrollData>> data;
        switch (mode){
            case "Day":
                data = tracker.getScrollData(dateToday);
                break;
            case "Week":
                String datePrevWeek = LocalDate.now().minusDays(7).toString();
                data = tracker.getScrollData(datePrevWeek, dateToday);
                break;
            case "Month":
                String datePrevMonth = LocalDate.now().minusMonths(1).toString();
                data = tracker.getScrollData(datePrevMonth, dateToday);
                break;
            default:
                data = tracker.getScrollData();
        }

        return data;


        // depending on the mode calculate the correct range
        // clean / add values
        // query the scrollTracker
    }

    void drawGraph(Set<Map.Entry<String, ScrollData>> data, String mode){
        // based on the mode parse the data into week/ day collectives
        // put the data into a format the grpah can use
        // display

    }

    void setAppInfo(Set<Map.Entry<String, ScrollData>> data) {

        List<Map.Entry<String, Pair<Double, String>>> sortedList = new ArrayList<>();
        for (Map.Entry<String, ScrollData> entry : data) {
            sortedList.addAll(entry.getValue().getDataMap().entrySet());
        }

        List<AppEntry> apps = new ArrayList<>();

        // Sort by scroll distance descending
        Collections.sort(sortedList, new ScrollDataComparator());

        for (Map.Entry<String, Pair<Double, String>> entry : sortedList) {
            String appName = entry.getValue().second;
            double distance = entry.getValue().first;
            Drawable appIcon = tracker.getAppIcon(entry.getKey());
            apps.add(new AppEntry(entry.getKey(),appName, distance, appIcon));
        }

        recyclerViewApps.setAdapter(new AppEntryAdapter(apps));


    }

}