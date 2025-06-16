package com.ScrollTracker.scrolltracker;

import android.util.Pair;

import java.util.Comparator;
import java.util.Map;

public class ScrollDataComparator implements Comparator<Map.Entry<String, Pair<Double, String>>> {
    @Override
    public int compare(Map.Entry<String, Pair<Double, String>> first, Map.Entry<String, Pair<Double, String>> second) {
        // sort by scroll distance and of they are the same then appname
        int double_compare = Double.compare(second.getValue().first, first.getValue().first);
        if(double_compare != 0) return double_compare;
        return second.getValue().second.compareTo(first.getValue().second);
    }
}
