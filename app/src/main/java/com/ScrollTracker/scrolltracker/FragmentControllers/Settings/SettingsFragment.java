package com.ScrollTracker.scrolltracker.FragmentControllers.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.ScrollTracker.scrolltracker.ScrollService.ScrollTracker;
import com.example.scrolltracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private SwitchMaterial switchNotifications;
    private MaterialCardView themeView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Get SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);

        /*
        // handle theme
        themeView = view.findViewById(R.id.themeSelect);
        themeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openThemeDialog(view);
            }
        });

        // Handle Notifications Toggle
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(getContext(), "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });
        */

        MaterialCardView credits_card = view.findViewById(R.id.creditsShow);
        credits_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();

                transaction.setCustomAnimations(
                        R.anim.slide_in_right,  // enter
                        R.anim.stay_put,        // exit (current fragment stays)
                        R.anim.stay_put_back,   // popEnter (when coming back, underlying fragment stays)
                        R.anim.slide_out_right  // popExit (new fragment slides out)
                );

                transaction.replace(R.id.fragment_container_view, new CreditsFragment());
                transaction.addToBackStack(null);  // Enables back navigation
                transaction.commit();
            }
        });

        // Handle Clear Data
        MaterialButton btnClearData = view.findViewById(R.id.btnClearData);
        btnClearData.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Clear Data")
                    .setMessage("Are you sure you want to clear all data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sharedPreferences.edit().clear().apply();
                        try {
                            ScrollTracker.clearData(getContext());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
    }


}

