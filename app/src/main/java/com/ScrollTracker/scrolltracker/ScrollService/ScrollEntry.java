package com.ScrollTracker.scrolltracker.ScrollService;

public class ScrollEntry {
    private double distance;
    private String appName;

    // Required no-arg constructor for Jackson
    public ScrollEntry() {}

    public ScrollEntry(double value, String label) {
        this.distance = value;
        this.appName = label;
    }

    // Getters and setters
    public double getDistance() { return distance; }
    public void setDistance(double value) { this.distance = value; }

    public String getAppName() { return appName; }
    public void setAppName(String label) { this.appName = label; }
}
