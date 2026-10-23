package com.dikeledi.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;
    private Switch switchAlerts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = getContext().getSharedPreferences("SmartPantryPrefs", Context.MODE_PRIVATE);
        switchAlerts = view.findViewById(R.id.switchAlerts);

        // Load saved setting
        boolean alertsEnabled = prefs.getBoolean("alerts_enabled", true);
        switchAlerts.setChecked(alertsEnabled);

        // Save setting when toggled
        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("alerts_enabled", isChecked).apply();
        });

        return view;
    }
}