package com.example.tsuki;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_choice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Button color change when all fields filled
        AppCompatButton btnSignUp = findViewById(R.id.btnSignUp);
        TextInputEditText etName = (TextInputEditText) ((TextInputLayout) findViewById(R.id.inputFullName)).getEditText();
        TextInputEditText etEmail = (TextInputEditText) ((TextInputLayout) findViewById(R.id.inputEmail)).getEditText();
        TextInputEditText etPassword = (TextInputEditText) ((TextInputLayout) findViewById(R.id.inputPassword)).getEditText();

        // Set initial state (disabled/gray)
        btnSignUp.setBackgroundResource(R.drawable.bg_signup_button_disabled);
        btnSignUp.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                boolean filled = etName.getText() != null && !etName.getText().toString().isEmpty()
                        && etEmail.getText() != null && !etEmail.getText().toString().isEmpty()
                        && etPassword.getText() != null && !etPassword.getText().toString().isEmpty();

                btnSignUp.setEnabled(filled);
                btnSignUp.setBackgroundResource(filled
                        ? R.drawable.bg_signup_button
                        : R.drawable.bg_signup_button_disabled);
            }
        };

        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);

        // Register user to Firebase Auth on sign up
        btnSignUp.setOnClickListener(v -> {
            String name     = etName.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            btnSignUp.setEnabled(false);

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Simpan nama ke SharedPreferences untuk ditampilkan di profil
                            getSharedPreferences("user_data", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_name", name)
                                    .putString("user_email", email)
                                    .apply();

                            // Simpan profil ke Firestore
                            new FirestoreManager().saveProfile(name, email, null, null);

                            startActivity(new Intent(this, ProfileSetupActivity.class));
                        } else {
                            btnSignUp.setEnabled(true);
                            String msg = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Registration failed";
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // "Already have an account? Sign in"
        TextView tvSignIn = findViewById(R.id.tvSignIn);
        String full = "Already have an account? Sign in";
        SpannableString spannable = new SpannableString(full);
        int start = full.indexOf("Sign in");
        spannable.setSpan(
            new ForegroundColorSpan(ContextCompat.getColor(this, R.color.navy)),
            start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannable.setSpan(
            new StyleSpan(Typeface.BOLD),
            start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tvSignIn.setText(spannable);
        tvSignIn.setTextColor(ContextCompat.getColor(this, R.color.gray));
    }
}