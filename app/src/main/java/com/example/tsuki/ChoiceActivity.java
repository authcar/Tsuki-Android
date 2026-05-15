package com.example.tsuki;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class ChoiceActivity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choice);
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

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnSignUp).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpChoiceActivity.class)));

        findViewById(R.id.btnSignIn).setOnClickListener(v ->
                startActivity(new Intent(this, SignInActivity.class)));

        // "Continue with Google" button
        findViewById(R.id.btnGoogleChoice).setOnClickListener(v -> {
            googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
                startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);
            });
        });
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

                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnSuccessListener(result -> {
                            String name  = account.getDisplayName() != null ? account.getDisplayName() : "";
                            String email = account.getEmail() != null ? account.getEmail() : "";

                            getSharedPreferences("user_data", MODE_PRIVATE).edit()
                                    .putString("user_name", name)
                                    .putString("user_email", email)
                                    .apply();
                            new FirestoreManager().saveProfile(name, email, null, null);

                            // Cek apakah user baru atau sudah pernah login
                            boolean isNewUser = result.getAdditionalUserInfo() != null
                                    && result.getAdditionalUserInfo().isNewUser();

                            if (isNewUser) {
                                // User baru → onboarding
                                startActivity(new Intent(this, ProfileSetupActivity.class));
                            } else {
                                // User lama → langsung ke home
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
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
}
