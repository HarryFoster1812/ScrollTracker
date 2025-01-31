package com.example.scrolltracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private Switch switchNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Get SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);

        // Handle Notifications Toggle
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(getContext(), "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        // Handle Clear Data
        MaterialButton btnClearData = view.findViewById(R.id.btnClearData);
        btnClearData.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Clear Data")
                    .setMessage("Are you sure you want to clear all data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sharedPreferences.edit().clear().apply();
                        Toast.makeText(getContext(), "Data cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    public void openThemeDialog(View view) {
        String[] themes = {"Light Mode", "Dark Mode", "Blue Theme", "Green Theme"};

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Choose Theme")
                .setItems(themes, (dialog, which) -> {
                    switch (which) {
                        case 0: setTheme("light"); break;
                        case 1: setTheme("dark"); break;
                        case 2: setTheme("blue"); break;
                        case 3: setTheme("green"); break;
                    }
                })
                .show();
    }

    private void setTheme(String theme) {
        sharedPreferences.edit().putString("theme", theme).apply();
        requireActivity().recreate(); // Restart activity to apply theme
    }


}

