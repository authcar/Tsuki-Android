package com.example.tsuki;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Splash 2 detik lalu pindah ke onboarding
        new Handler().postDelayed(() -> {

            Intent i = new Intent(
                    SplashActivity.this,
                    Onboarding1Activity.class
            );

            startActivity(i);
            finish();

        }, 2000);
    }
}