package com.example.tsuki;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private TextView tvProfileLocation;

    // Launcher untuk request permission lokasi
    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    permissions -> {
                        boolean granted = Boolean.TRUE.equals(
                                permissions.get(Manifest.permission.ACCESS_FINE_LOCATION))
                                || Boolean.TRUE.equals(
                                permissions.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                        if (granted) {
                            fetchAndSaveLocation();
                        } else {
                            if (isAdded()) tvProfileLocation.setText("Location not available");
                        }
                    });

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
        tvProfileLocation      = view.findViewById(R.id.tvProfileLocation);

        // ── Nama & email dari SharedPreferences (cepat) ──
        SharedPreferences userPrefs = requireContext()
                .getSharedPreferences("user_data", android.content.Context.MODE_PRIVATE);
        TextView tvName  = view.findViewById(R.id.tvProfileName);
        TextView tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvName.setText(userPrefs.getString("user_name", "User"));
        tvEmail.setText(userPrefs.getString("user_email", ""));

        // Tampilkan lokasi yang tersimpan di cache terlebih dahulu
        String cachedCity    = userPrefs.getString("user_city", null);
        String cachedCountry = userPrefs.getString("user_country", null);
        if (cachedCity != null && !cachedCity.isEmpty()) {
            tvProfileLocation.setText(formatLocation(cachedCity, cachedCountry));
        }

        // ── Sync dari Firestore ──
        new FirestoreManager().getProfile(data -> {
            if (!isAdded() || data == null) return;
            String name    = (String) data.get("name");
            String email   = (String) data.get("email");
            String city    = (String) data.get("city");
            String country = (String) data.get("country");

            if (name  != null) tvName.setText(name);
            if (email != null) tvEmail.setText(email);

            if (city != null && !city.isEmpty()) {
                tvProfileLocation.setText(formatLocation(city, country));
                // Update cache
                userPrefs.edit()
                        .putString("user_city", city)
                        .putString("user_country", country != null ? country : "")
                        .apply();
            } else {
                // Belum ada lokasi di Firestore → deteksi sekarang
                detectLocation();
            }

            userPrefs.edit()
                    .putString("user_name", name)
                    .putString("user_email", email)
                    .apply();
        }, e -> {
            // Firestore gagal — coba deteksi lokasi dari device
            if (isAdded() && cachedCity == null) detectLocation();
        });

        // ── Menu button ──
        view.findViewById(R.id.btnMenuProfile).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), NotificationActivity.class)));

        // ── Switches ──
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("settings", android.content.Context.MODE_PRIVATE);
        switchReminder.setChecked(prefs.getBoolean("reminder_enabled", true));
        switchDailyTips.setChecked(prefs.getBoolean("daily_tips_enabled", true));
        switchReminder.setOnCheckedChangeListener((btn, isChecked) ->
                prefs.edit().putBoolean("reminder_enabled", isChecked).apply());
        switchDailyTips.setOnCheckedChangeListener((btn, isChecked) ->
                prefs.edit().putBoolean("daily_tips_enabled", isChecked).apply());

        // ── Help Center ──
        view.findViewById(R.id.cardHelpCenter).setOnClickListener(v -> {
            // TODO: buka halaman Help Center
        });

        // ── Log Out ──
        view.findViewById(R.id.btnLogOut).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            com.google.android.gms.auth.api.signin.GoogleSignIn
                    .getClient(requireContext(),
                            new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                    com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .build())
                    .signOut()
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(requireContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
        });
    }

    // ─── Location ─────────────────────────────────────────────────────────────

    /**
     * Cek permission dulu, lalu ambil lokasi.
     */
    private void detectLocation() {
        if (!isAdded()) return;

        boolean fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            fetchAndSaveLocation();
        } else {
            // Minta permission
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    /**
     * Ambil lokasi dari device dan simpan ke Firestore + SharedPreferences.
     */
    private void fetchAndSaveLocation() {
        if (!isAdded()) return;
        tvProfileLocation.setText("Detecting location...");

        LocationHelper.getLocation(requireContext(),
                (city, country) -> {
                    if (!isAdded()) return;
                    tvProfileLocation.setText(formatLocation(city, country));

                    // Simpan ke Firestore
                    new FirestoreManager().saveLocation(city, country, null, null);

                    // Simpan ke cache lokal
                    requireContext()
                            .getSharedPreferences("user_data", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putString("user_city", city)
                            .putString("user_country", country)
                            .apply();
                },
                errorMsg -> {
                    if (!isAdded()) return;
                    tvProfileLocation.setText("Location unavailable");
                });
    }

    private String formatLocation(String city, String country) {
        if (city == null || city.isEmpty()) return country != null ? country : "";
        if (country == null || country.isEmpty()) return city;
        return city + ", " + country;
    }
}
