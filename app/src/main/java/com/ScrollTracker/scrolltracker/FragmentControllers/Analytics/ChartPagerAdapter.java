package com.ScrollTracker.scrolltracker.FragmentControllers.Analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ScrollTracker.scrolltracker.ScrollService.ScrollData;
import com.example.scrolltracker.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ChartPagerAdapter extends FragmentStateAdapter {

    List<List<android.util.Pair<String, Double>>> chartData;
    public ChartPagerAdapter(@NonNull Fragment fragment, List<List<android.util.Pair<String, Double>>> chartData) {
        super(fragment);
        this.chartData = chartData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BarChartFragment(chartData.get(0));
            case 1:
                return new LineChartFragment(chartData.get(1), "Week");
            case 2:
                return new LineChartFragment(chartData.get(3), "Month");
            default:
                throw new IndexOutOfBoundsException("The given index is out of bounds");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

