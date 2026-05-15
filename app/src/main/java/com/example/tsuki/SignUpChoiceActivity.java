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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignUpChoiceActivity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

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

        // Setup Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Google Sign In button
        findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            // Revoke dulu agar picker selalu muncul (tidak auto-login akun lama)
            googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            });
        });

        // Email/Password Sign Up
        AppCompatButton btnSignUp = findViewById(R.id.btnSignUp);
        TextInputEditText etName     = (TextInputEditText) ((TextInputLayout) findViewById(R.id.inputFullName)).getEditText();
        TextInputEditText etEmail    = (TextInputEditText) ((TextInputLayout) findViewById(R.id.inputEmail)).getEditText();
        TextInputEditText etPassword = (TextInputEditText) ((TextInputLayout) findViewById(R.id.inputPassword)).getEditText();

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

        btnSignUp.setOnClickListener(v -> {
            String name     = etName.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            btnSignUp.setEnabled(false);

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveUserAndProceed(name, email);
                        } else {
                            btnSignUp.setEnabled(true);
                            showSignUpErrorDialog(task.getException());
                        }
                    });
        });

        // "Already have an account? Sign in"
        TextView tvSignIn = findViewById(R.id.tvSignIn);
        String full = "Already have an account? Sign in";
        SpannableString spannable = new SpannableString(full);
        int start = full.indexOf("Sign in");
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.navy)),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignIn.setText(spannable);
        tvSignIn.setTextColor(ContextCompat.getColor(this, R.color.gray));
    }

    // ─── Google Sign In result ────────────────────────────────────────────────

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn
                        .getSignedInAccountFromIntent(data).getResult();
                AuthCredential credential = GoogleAuthProvider
                        .getCredential(account.getIdToken(), null);

                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener(result -> {
                            String name  = account.getDisplayName() != null
                                    ? account.getDisplayName() : "";
                            String email = account.getEmail() != null
                                    ? account.getEmail() : "";
                            saveUserAndProceed(name, email);
                        })
                        .addOnFailureListener(e ->
                                android.widget.Toast.makeText(this,
                                        "Google Sign In failed: " + e.getMessage(),
                                        android.widget.Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                android.widget.Toast.makeText(this,
                        "Google Sign In cancelled.",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ─── Helper: simpan user dan navigate ────────────────────────────────────

    private void saveUserAndProceed(String name, String email) {
        getSharedPreferences("user_data", MODE_PRIVATE)
                .edit()
                .putString("user_name", name)
                .putString("user_email", email)
                .apply();
        new FirestoreManager().saveProfile(name, email, null, null);
        startActivity(new Intent(this, ProfileSetupActivity.class));
    }

    // ─── Error dialog ─────────────────────────────────────────────────────────

    private void showSignUpErrorDialog(Exception exception) {
        String title, message, positiveButton;
        Runnable positiveAction = null;

        if (exception instanceof FirebaseAuthUserCollisionException) {
            title          = "Email Already Registered";
            message        = "This email is already associated with an account.\n\nWould you like to sign in instead?";
            positiveButton = "Sign In";
            positiveAction = () -> {
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            };
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            title          = "Password Too Weak";
            message        = "Your password must be at least 6 characters long.";
            positiveButton = "OK";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            title          = "Invalid Email";
            message        = "Please enter a valid email address (e.g. audrey@gmail.com).";
            positiveButton = "OK";
        } else {
            title          = "Sign Up Failed";
            message        = exception != null ? exception.getMessage() : "An unexpected error occurred.";
            positiveButton = "OK";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true);

        Runnable finalAction = positiveAction;
        builder.setPositiveButton(positiveButton, (dialog, which) -> {
            dialog.dismiss();
            if (finalAction != null) finalAction.run();
        });
        if (positiveAction != null) {
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        }
        builder.show();
    }
}
