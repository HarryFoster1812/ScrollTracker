package com.example.scrolltracker;
import android.accessibilityservice.AccessibilityService;
import android.view.WindowManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;
import android.content.Intent;

public class ScrollAccessibilityService extends AccessibilityService {

    int ResolutionHeight;
    int ResolutionWidth;
    int DPI;
    float distance;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED){
            Log.d("ScrollService", "Scroll Detected Y:" + event.getScrollDeltaY() + " X:"+ event.getScrollDeltaX());
            distance += (Math.abs(event.getScrollDeltaY())/DPI)*2.54;
            distance += (Math.abs(event.getScrollDeltaX())/DPI)*2.54;

            Intent intent = new Intent("com.example.scrolltracker.DISTANCE_UPDATED");
            intent.putExtra("distance", distance);
            sendBroadcast(intent);
            Log.d("ScrollService", "Broadcast sent!");

        }
    }

    @Override
    public void onInterrupt(){
        // do something
    }

    @Override
    protected void onServiceConnected(){
        super.onServiceConnected();
        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        ResolutionWidth = displayMetrics.widthPixels;
        ResolutionHeight = displayMetrics.heightPixels;
        DPI = displayMetrics.densityDpi;
    }
}
