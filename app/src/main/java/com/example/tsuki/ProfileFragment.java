package com.example.tsuki;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Switch switchReminder  = view.findViewById(R.id.switchReminder);
        Switch switchDailyTips = view.findViewById(R.id.switchDailyTips);

        // Menu button
        view.findViewById(R.id.btnMenuProfile).setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), NotificationActivity.class)));

        // Simpan state switch ke SharedPreferences
        android.content.SharedPreferences prefs = requireContext()
                .getSharedPreferences("settings", android.content.Context.MODE_PRIVATE);

        switchReminder.setChecked(prefs.getBoolean("reminder_enabled", true));
        switchDailyTips.setChecked(prefs.getBoolean("daily_tips_enabled", true));

        switchReminder.setOnCheckedChangeListener((btn, isChecked) ->
                prefs.edit().putBoolean("reminder_enabled", isChecked).apply());

        switchDailyTips.setOnCheckedChangeListener((btn, isChecked) ->
                prefs.edit().putBoolean("daily_tips_enabled", isChecked).apply());

        // Help Center
        view.findViewById(R.id.cardHelpCenter).setOnClickListener(v -> {
            // TODO: buka halaman Help Center
        });

        // Log Out — kembali ke SplashActivity dan clear semua back stack
        view.findViewById(R.id.btnLogOut).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
