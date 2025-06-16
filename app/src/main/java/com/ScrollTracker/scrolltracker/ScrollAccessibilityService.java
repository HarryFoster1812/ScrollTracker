package com.ScrollTracker.scrolltracker;
import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.view.WindowManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import android.content.Intent;

import androidx.annotation.RequiresApi;

import com.example.scrolltracker.R;

public class ScrollAccessibilityService extends AccessibilityService {

    int ResolutionHeight;
    int ResolutionWidth;
    int DPI;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event){
        int eventType = event.getEventType();
        Log.d("ScrolAccessibilitylEvent", AccessibilityEvent.eventTypeToString(eventType));

        switch (eventType){
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:

                Log.d("ScrollService", "Scroll Detected Y:" + event.getScrollDeltaY() + " X:"+ event.getScrollDeltaX());
                double distance = 0;
                distance += (Math.abs((double)event.getScrollDeltaY())/(double)DPI)*2.54;
                distance += (Math.abs((double)event.getScrollDeltaX())/(double)DPI)*2.54;

                Intent intent = new Intent("com.example.scrolltracker.DISTANCE_UPDATED");
                intent.putExtra("distance", distance);
                intent.putExtra("package", event.getPackageName());
                sendBroadcast(intent);
                Log.d("ScrollService", "Broadcast sent!");

                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                AccessibilityNodeInfo source = getRootInActiveWindow();
                AccessibilityNodeInfo eventsource = event.getSource();
                int eventAction = event.getAction();
                float DeltaY = event.getScrollDeltaY();
                float DeltaX = event.getScrollDeltaX();
                if (source != null) {
                    // Try to check the scroll position using AccessibilityNodeInfo
                    // This requires traversing the accessibility tree or querying the scroll properties of the node
                    Log.d("ScrollService", "Root is not not null.");
                }
                Log.d("WINDOWCONTENTCHANGED", "DeltaY:" + DeltaY + ", DeltaX:" + DeltaX);
                if (DeltaX != -1 || DeltaY !=-1){
                    float deltaX = event.getScrollDeltaX();
                    float deltaY = event.getScrollDeltaY();
                }
                break;
        }
    }

    @Override
    public void onInterrupt(){
        stopForeground(true);
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

        startForegroundNotification();
    }

    private void startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "scroll_tracker_channel";
            String channelName = "Scroll Tracker Service";

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Keeps Scroll Tracker running");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle("Scroll Tracker Active")
                    .setContentText("Tracking scroll distance")
                    .setSmallIcon(R.drawable.ic_scroll) // Use your icon here
                    .setOngoing(true) // Makes it non-dismissible
                    .build();

            startForeground(
                        1,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            // For older Android versions
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("Scroll Tracker Active")
                    .setContentText("Tracking scroll distance")
                    .setSmallIcon(R.drawable.ic_scroll)
                    .setOngoing(true)
                    .build();

            startForeground(1, notification);
        }
    }

}
