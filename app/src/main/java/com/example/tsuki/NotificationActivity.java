package com.example.tsuki;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

        // Tap card reminder → buka TimePicker
        reminderCard.setOnClickListener(v -> {
            new TimePickerDialog(this, (picker, hour, minute) -> {
                String amPm = hour >= 12 ? "PM" : "AM";
                int h = hour % 12 == 0 ? 12 : hour % 12;
                tvReminderTime.setText(String.format("%d:%02d %s", h, minute, amPm));
            }, 20, 0, false).show();
        });

        // Tombol Enable Notifications
        btnEnable.setOnClickListener(v -> requestNotificationPermission());

        // Tombol Maybe Later — langsung ke MainActivity tanpa minta permission
        btnLater.setOnClickListener(v -> navigateToMain());
    }

    private void requestNotificationPermission() {
        // Android 13+ (API 33) wajib minta permission POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Sudah granted — langsung lanjut
                navigateToMain();
            } else {
                // Minta permission — dialog sistem muncul
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Android 12 ke bawah — notifikasi otomatis aktif
            navigateToMain();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
