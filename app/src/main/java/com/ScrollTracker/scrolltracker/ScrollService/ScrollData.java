package com.ScrollTracker.scrolltracker.ScrollService;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ScrollData {
    @JsonProperty("distanceMap")
    private Map<String, ScrollEntry> distanceMap; //  Map<PackageName, Pair<Distance(cm), AppName>>
    private Context context;

    public ScrollData() {
        this.distanceMap = new HashMap<>();
    }
    public ScrollData(Context context) {
        this.distanceMap = new HashMap<>();
        this.context = context;
    }

    public void addDistance(String packageName, double distance) {
        if (!containsPackage(packageName)) {
            String appName = getAppNameFromPackage(packageName);
            distanceMap.put(packageName, new ScrollEntry(distance, appName));
        } else {
            ScrollEntry entry = distanceMap.get(packageName);
            double currentDistance = entry.getDistance();
            double new_distance = currentDistance + distance;
            distanceMap.put(packageName, new ScrollEntry(new_distance, entry.getAppName()));
        }
    }

    public Map<String, ScrollEntry> getDataMap(){
        return this.distanceMap;
    }

    public double getDistance(String packageName) {
        return distanceMap.getOrDefault(packageName, new ScrollEntry(Double.MIN_VALUE, "")).getDistance();
    }

    public boolean containsPackage(String packageName) {
        return distanceMap.containsKey(packageName);
    }

    public double calculateTotal() {
        double total = 0;
        for (Map.Entry<String, ScrollEntry> entry : distanceMap.entrySet()) {
            total += entry.getValue().getDistance();
        }
        return total;
    }

    public String getAppNameFromPackage(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return packageName; // Fallback to package name if not found
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ScrollEntry> entry : distanceMap.entrySet())
            sb.append("Package: " + entry.getKey() + "\tAppname: " + entry.getValue().getAppName() + "\tDistance: " + entry.getValue().getDistance() + "\n");
        return sb.toString();
    }

}


