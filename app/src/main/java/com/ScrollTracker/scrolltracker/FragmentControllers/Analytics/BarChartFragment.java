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

import com.example.scrolltracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class BarChartFragment extends Fragment {

    private List<Pair<String, Double>> dataPairs;
    public BarChartFragment(List<Pair<String, Double>> dataInput){
        dataPairs = dataInput;
        // do something with the data
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);

        BarChart barChart = view.findViewById(R.id.barChart);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < dataPairs.size(); i++) {
            Pair<String, Double> pair = dataPairs.get(i);
            entries.add(new BarEntry(i, pair.second.floatValue()));
            labels.add(pair.first);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Scroll Distance");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(12f);

        BarData barDataSet = new BarData(dataSet);
        barDataSet.setBarWidth(0.9f);

        barChart.setData(barDataSet);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);


        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisLineColor(Color.WHITE);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate();

        return view;
    }
}
