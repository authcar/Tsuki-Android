package com.example.tsuki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

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

        // Tampilkan nama & email dari SharedPreferences dulu (cepat)
        SharedPreferences userPrefs = requireContext()
                .getSharedPreferences("user_data", android.content.Context.MODE_PRIVATE);
        TextView tvName  = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvName.setText(userPrefs.getString("user_name", "User"));
        tvEmail.setText(userPrefs.getString("user_email", ""));

        // Sync dari Firestore (update jika ada data terbaru)
        new FirestoreManager().getProfile(data -> {
            if (data != null) {
                String name  = (String) data.get("name");
                String email = (String) data.get("email");
                if (name  != null) tvName.setText(name);
                if (email != null) tvEmail.setText(email);
                // Update SharedPreferences juga
                userPrefs.edit()
                        .putString("user_name", name)
                        .putString("user_email", email)
                        .apply();
            }
        }, null);

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

        // Log Out — Firebase sign out + kembali ke SplashActivity
        view.findViewById(R.id.btnLogOut).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
