package com.example.scrolltracker;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ScrollData {
    @JsonProperty("distanceMap")
    private Map<String, Float> distanceMap;

    public ScrollData() {
        this.distanceMap = new HashMap<>();
    }

    public void addDistance(String packageName, float distance) {
        if (!containsPackage(packageName)) {
            distanceMap.put(packageName, distance);
        }
        else{
            float currentDistance = distanceMap.get(packageName);
            distanceMap.put(packageName, currentDistance + distance);
        }
    }

    public float getDistance(String packageName) {
        return distanceMap.getOrDefault(packageName, 0f);
    }

    public boolean containsPackage(String packageName) {
        return distanceMap.containsKey(packageName);
    }

    public float calculateTotal(){
        float total = 0;
        for (Map.Entry<String, Float> entry : distanceMap.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }
}
