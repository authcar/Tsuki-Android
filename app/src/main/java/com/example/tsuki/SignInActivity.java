package com.example.tsuki;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText etEmail, etPassword;
    private TextInputLayout inputEmailLayout, inputPasswordLayout;
    private AppCompatButton btnSignIn;
    private TextView tvWrongPassword;
    private CheckBox checkTerms;

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btnGoogle).setOnClickListener(v -> {
            googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
                startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);  // konek ke google
            });
        });


        etEmail        = findViewById(R.id.etEmail);
        etPassword     = findViewById(R.id.etPassword);
        inputEmailLayout    = findViewById(R.id.inputEmailLayout);
        inputPasswordLayout = findViewById(R.id.inputPasswordLayout);
        btnSignIn      = findViewById(R.id.btnSignIn);
        tvWrongPassword = findViewById(R.id.tvWrongPassword);
        checkTerms     = findViewById(R.id.checkTerms);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Enable button when fields filled
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        };
        etEmail.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        checkTerms.setOnCheckedChangeListener((btn, checked) -> updateButtonState());

        // Sign In button
        btnSignIn.setOnClickListener(v -> signIn());

        // Forgot password
        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            // Kirim email yang sudah diisi jika ada
            if (etEmail.getText() != null && !etEmail.getText().toString().isEmpty()) {
                intent.putExtra("email", etEmail.getText().toString().trim());
            }
            startActivity(intent);
        });

        // Sign up link
        setupSignUpLink();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn
                        .getSignedInAccountFromIntent(data).getResult();
                AuthCredential credential = GoogleAuthProvider
                        .getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnSuccessListener(result -> {
                            // Simpan nama & email
                            getSharedPreferences("user_data", MODE_PRIVATE).edit()
                                    .putString("user_name", account.getDisplayName())
                                    .putString("user_email", account.getEmail())
                                    .apply();
                            // Navigate ke MainActivity
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e ->
                                android.widget.Toast.makeText(this,
                                        "Google Sign In failed: " + e.getMessage(),
                                        android.widget.Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                android.widget.Toast.makeText(this, "Cancelled.", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateButtonState() {
        boolean emailFilled    = etEmail.getText() != null && !etEmail.getText().toString().isEmpty();
        boolean passwordFilled = etPassword.getText() != null && !etPassword.getText().toString().isEmpty();
        boolean termsChecked   = checkTerms.isChecked();
        boolean enabled = emailFilled && passwordFilled && termsChecked;

        btnSignIn.setEnabled(enabled);
        btnSignIn.setBackgroundResource(enabled
                ? R.drawable.bg_signup_button
                : R.drawable.bg_signup_button_disabled);
    }

    private void signIn() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Reset error state
        tvWrongPassword.setVisibility(View.GONE);
        inputPasswordLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.gray));

        btnSignIn.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login berhasil → ke MainActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        // Login gagal → tampilkan error
                        btnSignIn.setEnabled(true);
                        showPasswordError();
                    }
                });
    }

    private void showPasswordError() {
        tvWrongPassword.setVisibility(View.VISIBLE);
        // Ubah border input password menjadi merah
        inputPasswordLayout.setBoxStrokeColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        inputPasswordLayout.setBoxStrokeErrorColor(
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(this, android.R.color.holo_red_light)));
    }

    private void setupSignUpLink() {
        TextView tvSignUp = findViewById(R.id.tvSignUp);
        String full = "Don't have an account? Sign up";
        SpannableString spannable = new SpannableString(full);
        int start = full.indexOf("Sign up");
        spannable.setSpan(new ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.navy)),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                start, full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignUp.setText(spannable);
        tvSignUp.setTextColor(ContextCompat.getColor(this, R.color.gray));
        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpChoiceActivity.class)); // Mengarahkan ke SignUpChoiceActivity
            finish();
        });
    }
}
