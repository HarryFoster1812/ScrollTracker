package com.ScrollTracker.scrolltracker.ScrollService;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.content.Context;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Set;

public class ScrollTracker {
    @JsonProperty("dataMap")
    private static Map<String, ScrollData> scrollData;
    private Context context;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String fileName = "scroll_data.json";

    // Constructor for ScrollTracker
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ScrollTracker(Context context){
        this.context = context;
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()){
            // Load data from file
            scrollData = loadScrollDataFromFile();
        }
        else{
            scrollData = new HashMap<>();
            scrollData.put(LocalDate.now().toString(), new ScrollData(this.context));
            writeScrollDataToFile();
        }
    }

    // Load ScrollData from JSON file
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Map<String, ScrollData> loadScrollDataFromFile(){
        try {
            // Set up a custom date format for Jackson
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            objectMapper.setDateFormat(dateFormat);

            // Deserialize the JSON file to a Map<Date, ScrollData>
            File jsonFile = new File(context.getFilesDir(), fileName);
            return objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, ScrollData.class));

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>(); // Return an empty map if there's an error
        }
    }

    public static void clearData(Context context) throws IOException {
        scrollData = new HashMap<>();
        File jsonFile = new File(context.getFilesDir(), fileName);
        if(!jsonFile.exists()) return;
        objectMapper.writeValue(jsonFile, scrollData);
    }

    // Write ScrollData to a JSON file
    private void writeScrollDataToFile(){
        try {
            // Set up a custom date format for Jackson (if you haven't already set it globally)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            objectMapper.setDateFormat(dateFormat);

            // Serialize the Map<LocalDate, ScrollData> to JSON and write it to the file
            File jsonFile = new File(context.getFilesDir(), fileName);
            objectMapper.writeValue(jsonFile, scrollData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDistance(String packageName, double distance){
        // try and get the scroll data
        ScrollData result = scrollData.get(LocalDate.now().toString());

        if (result != null){
            result.addDistance(packageName, distance);
        }

        else{
            result = new ScrollData(this.context);
            result.addDistance(packageName, distance);
            scrollData.put(LocalDate.now().toString(), result);
        }
        writeScrollDataToFile();

    }

    public double getTotalDistance(LocalDate date){
        ScrollData result = scrollData.get(date.toString());

        if (result == null){
            return 0;
        }

        return result.calculateTotal();
    }

    public Set<Map.Entry<String, ScrollData>> getScrollData(){
        return this.scrollData.entrySet();
    }

    public Set<Map.Entry<String, ScrollData>> getScrollData(String date){
        try{
            ScrollData data = scrollData.get(date);
            Map<String, ScrollData> dataMap = new HashMap<>();
            dataMap.put(date, data);
            return dataMap.entrySet();

        } catch(Exception e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    public Set<Map.Entry<String, ScrollData>> getScrollData(String startDate, String endDate){
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);

        try{

            Set<Map.Entry<String, ScrollData>> dataSet = new ArraySet<>();
            for (Map.Entry<String, ScrollData> entry: scrollData.entrySet()) {

                LocalDate entryDate = LocalDate.parse(entry.getKey().toString());
                if(entryDate.compareTo(startLocalDate) >= 0 && entryDate.compareTo(endLocalDate) <=0){
                    dataSet.add(entry);
                }
            }
            return dataSet;

        } catch(Exception e) {
            return null;
        }
    }

    public Drawable getAppIcon(String packageName){
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch(Exception e){
            Drawable placeholder = new ColorDrawable(Color.GRAY);
            placeholder.setBounds(0, 0, 192, 192); // Typical icon size in px
            return placeholder;
        }
    }

    public void logScrollData(){
        for (Map.Entry<String, ScrollData> entry : scrollData.entrySet()) {
            Log.d("SCROLLDATA: " + entry.getKey(), "logScrollData: " + entry.getValue().toString());
        }
    }
}
