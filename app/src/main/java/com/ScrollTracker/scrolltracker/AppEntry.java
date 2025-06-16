package com.ScrollTracker.scrolltracker;

import android.graphics.drawable.Drawable;

public class AppEntry{
    String packageName;
    String appName;
    Double distance;
    Drawable icon;
    public AppEntry(String packageName, String appName, Double distance ,Drawable icon){
        this.appName = appName;
        this.icon = icon;
        this.distance = distance;
        this.packageName = packageName;
    }
}

