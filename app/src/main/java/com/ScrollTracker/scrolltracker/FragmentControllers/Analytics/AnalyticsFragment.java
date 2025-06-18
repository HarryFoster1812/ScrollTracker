package com.ScrollTracker.scrolltracker.FragmentControllers.Analytics;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.ScrollTracker.scrolltracker.ScrollService.ScrollData;
import com.ScrollTracker.scrolltracker.ScrollService.ScrollEntry;
import com.ScrollTracker.scrolltracker.ScrollService.ScrollTracker;
import com.example.scrolltracker.R;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AnalyticsFragment extends Fragment {

    private ViewPager2 viewPagerCharts;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewApps;
    private AppEntryAdapter adapter;

    private ScrollTracker tracker;

    private boolean eventListenerRunning;

    List<Set<Map.Entry<String, ScrollData>>> data= new ArrayList<Set<Map.Entry<String, ScrollData>>>(3);

    List<List<Pair<String, Double>>> graphData = new ArrayList<>(3);

    Map<Integer, List<AppEntry>> cachedAppEntries = new HashMap<>();

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
        Runnable runnable = () -> {
            getScrollData();
            parseScrollDataForGraph();
            requireActivity().runOnUiThread(() -> {
                // Safe to touch views here
                viewPagerCharts.setAdapter(new ChartPagerAdapter(this, graphData));
                viewPagerCharts.setCurrentItem(0);
            });
        };
        Thread t = new Thread(runnable);
        t.start();
        // add to the viewPager

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Runnable swipeRefreshRunnable = () -> {
                cachedAppEntries.clear();
                data.clear();
                graphData.clear();
                getScrollData();
                parseScrollDataForGraph();
                requireActivity().runOnUiThread(() -> {
                    // Safe to touch views here
                    viewPagerCharts.setAdapter(new ChartPagerAdapter(AnalyticsFragment.this, graphData));
                    viewPagerCharts.setCurrentItem(0);
                    swipeRefreshLayout.setRefreshing(false);
                });
            };
            Thread t1 = new Thread(swipeRefreshRunnable);
            t1.start();
        });

        viewPagerCharts.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(!eventListenerRunning) {
                    eventListenerRunning = true;
                    // This is triggered when the page is changed (swiped)
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                    eventListenerRunning = false;
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });

        // create a thread that will load and get all the data then it will create and bind the graphs in a ui thread

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


        // Connect viewPager to tabs
        tabLayout.post(() -> tabLayout.post(()->handleTab(tabLayout.getTabAt(0)))); // manually handle the first tab

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    private void handleTab(TabLayout.Tab tab){
        if(!eventListenerRunning) {
            eventListenerRunning = true;
            int selectedIndex = tab.getPosition();

            viewPagerCharts.setCurrentItem(selectedIndex);
            changeGraph(selectedIndex);
            setAppInfo(selectedIndex);
            eventListenerRunning = false;
        }
         // trigger chart to change
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getScrollData(){
        // get today date
        String dateToday = LocalDate.now().toString();
        data.add(tracker.getScrollData(dateToday));

        String datePrevWeek = LocalDate.now().minusDays(7).toString();
        data.add(tracker.getScrollData(datePrevWeek, dateToday));

        String datePrevMonth = LocalDate.now().minusMonths(1).toString();
        data.add(tracker.getScrollData(datePrevMonth, dateToday));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void parseScrollDataForGraph(){
        parseBarData();
        parseLineData("Week");
        parseLineData("Month");
    }

    void parseBarData(){
        Set<Map.Entry<String, ScrollData>> rawData = data.get(0);
        List<Pair<String, Double>> barData = new ArrayList<>();

        if (rawData.isEmpty() || rawData.iterator().next().getValue() == null){
            graphData.add(barData);
            return;
        }

        for(Map.Entry<String, ScrollData> dateEntry: rawData){
            for(Map.Entry<String, ScrollEntry> entry: dateEntry.getValue().getDataMap().entrySet()){
                barData.add(new Pair<>(entry.getValue().getAppName(), entry.getValue().getDistance()));
            }
        }

        graphData.add(barData);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void parseLineData(String mode){

        Set<Map.Entry<String, ScrollData>> rawData = data.get(0);
        List<Pair<String, Double>> chartData = new ArrayList<>();

        if (rawData.isEmpty() || rawData.iterator().next().getValue() == null){
            graphData.add(chartData);
            return;
        }

        List<String> dates = new ArrayList<>();
        LocalDate tempDate;
        switch(mode){
            case "Week": tempDate = LocalDate.now().minusWeeks(1); break;
            case "Month": tempDate= LocalDate.now().minusMonths(1); break;
            default: throw new IllegalStateException("Not valid mode");
        };


        // get all dates
        while (!tempDate.equals(LocalDate.now().plusDays(1))){
            dates.add(tempDate.toString());
            tempDate = tempDate.plusDays(1);
        }

        for (String date : dates) {
            // Try to find the entry where the key equals the current date
            Map.Entry<String, ScrollData> match = rawData.stream()
                    .filter(entry -> entry.getKey().equals(date))
                    .findFirst()
                    .orElse(null);

            if (match != null) {
                chartData.add(new Pair<>(date, match.getValue().calculateTotal()));
            } else {
                chartData.add(new Pair<>(date, 0.0)); // No data for that day
            }
        }

        graphData.add(chartData);
    }


    void changeGraph(int position){
        // based on the mode parse the data into week/ day collectives
        // put the data into a format the graph can use
        // display
        viewPagerCharts.setCurrentItem(position, true);

    }

    void setAppInfo(int index) {

        if (cachedAppEntries.containsKey(index)) {
            adapter.setData(cachedAppEntries.get(index));
            return;
        }


        List<AppEntry> tempApps = new ArrayList<>();

        if(data.get(index).isEmpty() || data.get(index).iterator().next().getValue() == null){
            return;
        }

        for (Map.Entry<String, ScrollData> entry : data.get(index)) {
            for (Map.Entry<String, ScrollEntry> entry1 : entry.getValue().getDataMap().entrySet()) {
                // Copy the key and the Pair object
                String appName = entry1.getValue().getAppName();
                double distance = entry1.getValue().getDistance();
                Drawable appIcon = tracker.getAppIcon(entry1.getKey());
                tempApps.add(new AppEntry(entry1.getKey(),appName, distance, appIcon));
            }
        }

        List<AppEntry> apps = new ArrayList<>();

        // collapse the entries
        for(AppEntry app: tempApps){
            if(apps.stream().anyMatch(a -> a.packageName.equals(app.packageName))) continue;
            List<AppEntry> samePackage = tempApps.stream().filter(a -> a.packageName.equals(app.packageName) && a != app).collect(Collectors.toList());
            if (samePackage.size() != 0) continue;

            for(AppEntry duplicateApp: samePackage){
                app.distance += duplicateApp.distance;
            }
            apps.add(app);
        }

        // Add Sorting on the total distance
        Collections.sort(apps, new AppEntryComparator());
        cachedAppEntries.put(index, apps);
        adapter.setData(cachedAppEntries.get(index));

    }

}