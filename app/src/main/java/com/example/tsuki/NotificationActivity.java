package com.example.tsuki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotificationActivity extends AppCompatActivity {

    private TextView tvReminderTime;

    // Launcher untuk request permission (Android 13+)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        // Apapun hasilnya (granted/denied), lanjut ke MainActivity
                        navigateToMain();
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvReminderTime = findViewById(R.id.tvReminderTime);
        AppCompatButton btnEnable = findViewById(R.id.btnContinue);
        AppCompatButton btnLater  = findViewById(R.id.btnLater);
        CardView reminderCard     = findViewById(R.id.reminderCard);

        // Tampilkan jam yang tersimpan sebelumnya (default 20:00)
        android.content.SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);
        int savedHour   = prefs.getInt("reminder_hour", 20);
        int savedMinute = prefs.getInt("reminder_minute", 0);
        tvReminderTime.setText(formatTime(savedHour, savedMinute));

        // Tap card reminder → buka TimePicker
        reminderCard.setOnClickListener(v -> {
            int currentHour   = prefs.getInt("reminder_hour", 20);
            int currentMinute = prefs.getInt("reminder_minute", 0);
            new TimePickerDialog(this, (picker, hour, minute) -> {
                // Tampilkan jam yang dipilih
                tvReminderTime.setText(formatTime(hour, minute));
                // Simpan ke SharedPreferences
                prefs.edit()
                        .putInt("reminder_hour", hour)
                        .putInt("reminder_minute", minute)
                        .apply();
            }, currentHour, currentMinute, false).show();
        });

        // Tombol Enable Notifications
        btnEnable.setOnClickListener(v -> requestNotificationPermission());

        // Tombol Maybe Later — langsung ke MainActivity tanpa minta permission
        btnLater.setOnClickListener(v -> navigateToMain());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                requestBatteryOptimizationExemption();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            requestBatteryOptimizationExemption();
        }
    }

    @SuppressLint("BatteryLife")
    private void requestBatteryOptimizationExemption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.os.PowerManager pm =
                    (android.os.PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
                // Buka dialog sistem — minta user pilih "Allow"
                Intent intent = new Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return; // navigateToMain() dipanggil setelah user kembali
            }
        }
        navigateToMain();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setelah user kembali dari settings battery optimization → lanjut ke main
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.os.PowerManager pm =
                    (android.os.PowerManager) getSystemService(POWER_SERVICE);
            // Hanya navigate jika sudah pernah tekan Enable (bukan buka pertama kali)
            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())) {
                // Sudah exempt — boleh navigate, tapi cek dulu supaya tidak navigate saat pertama buka
            }
        }
    }

    private String formatTime(int hour, int minute) {
        String amPm = hour >= 12 ? "PM" : "AM";
        int h = hour % 12 == 0 ? 12 : hour % 12;
        return String.format(java.util.Locale.getDefault(), "%d:%02d %s", h, minute, amPm);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
