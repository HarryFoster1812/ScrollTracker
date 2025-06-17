package com.ScrollTracker.scrolltracker.FragmentControllers.Analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import com.example.scrolltracker.R;


public class LineChartFragment extends Fragment {

    List<Pair<String, Double>> lineData;
    public LineChartFragment(List<Pair<String, Double>> dataInput, String mode) {
        // fill any dates that are missing?
        lineData = dataInput;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        LineChart lineChart = view.findViewById(R.id.lineChart);

        List<Entry> entries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>();

        for (int i = 0; i < lineData.size(); i++) {
            Pair<String, Double> pair = lineData.get(i);
            entries.add(new Entry(i, pair.second.floatValue()));
            dateLabels.add(pair.first); // Keep dates for X-axis labels
        }

        LineDataSet dataSet = new LineDataSet(entries, "Total Scroll Distance");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Optional for smooth curves

        LineData lineDataSet = new LineData(dataSet);
        lineChart.setData(lineDataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelRotationAngle(-45); // Optional: rotate for readability
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisLineColor(Color.WHITE);

        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false); // Optional
        lineChart.setExtraBottomOffset(10f);
        lineChart.invalidate(); // Refresh chart


        return view;
    }
}
