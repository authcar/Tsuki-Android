package com.example.tsuki;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText etEmail;
    private TextView tvSuccess;
    private AppCompatButton btnSendReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth       = FirebaseAuth.getInstance();
        etEmail     = findViewById(R.id.etEmail);
        tvSuccess   = findViewById(R.id.tvSuccess);
        btnSendReset = findViewById(R.id.btnSendReset);

        // Pre-fill email jika dikirim dari SignInActivity
        String prefillEmail = getIntent().getStringExtra("email");
        if (prefillEmail != null) {
            etEmail.setText(prefillEmail);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnSendReset.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {
        String email = etEmail.getText() != null
                ? etEmail.getText().toString().trim() : "";

        if (email.isEmpty()) {
            etEmail.setError("Please enter your email");
            return;
        }

        btnSendReset.setEnabled(false);
        btnSendReset.setText("Sending...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    btnSendReset.setEnabled(true);
                    btnSendReset.setText("Send Reset Link");

                    if (task.isSuccessful()) {
                        // Tampilkan pesan sukses
                        tvSuccess.setVisibility(View.VISIBLE);
                        btnSendReset.setEnabled(false);
                    } else {
                        // Email tidak ditemukan atau error lain
                        etEmail.setError("Email not found. Please check and try again.");
                    }
                });
    }
}
