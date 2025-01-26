package com.example.scrolltracker;

import android.os.Build;
import android.content.Context;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

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
            scrollData.put(LocalDate.now().toString(), new ScrollData());
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
            result = new ScrollData();
            result.addDistance(packageName, distance);
            scrollData.put(LocalDate.now().toString(), result);
        }
        writeScrollDataToFile();

    }

    public double getTotalDistance(LocalDate date){
        ScrollData result = scrollData.get(date.toString());
        return result.calculateTotal();
    }

}
