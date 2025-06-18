package com.ScrollTracker.scrolltracker.FragmentControllers.Analytics;

import com.ScrollTracker.scrolltracker.ScrollService.ScrollEntry;

import java.util.Comparator;

public class AppEntryComparator implements Comparator<AppEntry> {
    @Override
    public int compare(AppEntry first, AppEntry  second) {
        // sort by scroll distance and of they are the same then appname
        int double_compare = Double.compare(second.distance, first.distance);
        if(double_compare != 0) return double_compare;
        return second.appName.compareTo(first.appName);
    }
}
