package com.example.tsuki;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileSetupActivity extends AppCompatActivity {

    private ImageView imgProgress;
    private TextView tvStep;
    private ImageButton btnBack;

    // Progress drawables per step (index 0 = step 1)
    private static final int[] PROGRESS_DRAWABLES = {
            R.drawable.progress1,
            R.drawable.progress2,
            R.drawable.progress3,
            R.drawable.progress4,
    };

    // Step labels per step (index 0 = step 1)
    private static final String[] STEP_LABELS = {
            "1 / 7", "2 / 7", "3 / 7", "4 / 7"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_setup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgProgress = findViewById(R.id.imgProgress);
        tvStep = findViewById(R.id.tvStep);
        btnBack = findViewById(R.id.btnBack);

        // Back button: pop fragment or finish activity if on first fragment
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        });

        // Load NameFragment as the first step
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new NameFragment())
                    .commit();
        }
    }

    /**
     * Show or hide the header (back button, progress bar, step label).
     * Used by LoadingFragment to hide chrome during loading.
     */
    public void setHeaderVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        btnBack.setVisibility(visibility);
        imgProgress.setVisibility(visibility);
        tvStep.setVisibility(visibility);
    }

    /**
     * Called by fragments to update the shared header (progress bar + step label).
     * @param step 1-based step number
     */
    public void setStep(int step) {
        int index = step - 1;
        if (index >= 0 && index < PROGRESS_DRAWABLES.length) {
            imgProgress.setImageResource(PROGRESS_DRAWABLES[index]);
            tvStep.setText(STEP_LABELS[index]);
        }
    }
}
