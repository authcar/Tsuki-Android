package com.example.tsuki;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        ImageButton btnLog = findViewById(R.id.btnLog);

        // Sembunyikan item Log bawaan BottomNavigationView (digantikan btnLog)
        bottomNav.getMenu().findItem(R.id.nav_log).setEnabled(false);
        bottomNav.getMenu().findItem(R.id.nav_log).setVisible(false);

        // Handle navigasi tab
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainer, new HomeFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_calendar) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainer, new CalendarFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_learn) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragmentContainer, new LearnFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_profile) {
                // TODO: tampilkan ProfileFragment
                return true;
            }
            return false;
        });

        // Handle tombol Log floating
        btnLog.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, new LogFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_log);
        });

        // Set tab awal
        bottomNav.setSelectedItemId(R.id.nav_home);

        // Load HomeFragment sebagai konten awal
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragmentContainer, new HomeFragment())
                    .commit();
        }
    }
}
