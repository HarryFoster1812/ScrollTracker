package com.ScrollTracker.scrolltracker;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ScrollData {
    @JsonProperty("distanceMap")
    private Map<String, Double> distanceMap;

    public ScrollData() {
        this.distanceMap = new HashMap<>();
    }

    public void addDistance(String packageName, double distance) {
        if (!containsPackage(packageName)) {
            distanceMap.put(packageName, distance);
        }
        else{
            double currentDistance = distanceMap.get(packageName);
            double new_distance = currentDistance + distance;
            distanceMap.put(packageName, new_distance);
        }
    }

    public double getDistance(String packageName) {
        return distanceMap.getOrDefault(packageName, Double.MIN_VALUE);
    }

    public boolean containsPackage(String packageName) {
        return distanceMap.containsKey(packageName);
    }

    public double calculateTotal(){
        double total = 0;
        for (Map.Entry<String, Double> entry : distanceMap.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Double> entry : distanceMap.entrySet())
        sb.append("Package: " +entry.getKey() + "\tDistance" + entry.getValue() + "\n");
        return sb.toString();
    }
}
