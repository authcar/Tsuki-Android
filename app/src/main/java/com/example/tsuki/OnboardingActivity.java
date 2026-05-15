package com.example.tsuki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager = findViewById(R.id.viewPager);
        btnNext   = findViewById(R.id.btnNext);
        DotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);

        // Setup adapter
        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Hubungkan dots ke ViewPager2
        dotsIndicator.attachTo(viewPager);

        // Update tombol saat halaman berubah
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateButton(position);
            }
        });

        // Tombol Next / Get Started
        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < adapter.getItemCount() - 1) {
                // Slide ke halaman berikutnya
                viewPager.setCurrentItem(current + 1, true);
            } else {
                // Halaman terakhir → ke ChoiceActivity
                startActivity(new Intent(this, ChoiceActivity.class));
                finish();
            }
        });
    }

    private void updateButton(int position) {
        if (position == 2) {
            btnNext.setText("Get Started");
        } else {
            btnNext.setText("Next");
        }
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    static class OnboardingAdapter extends FragmentStateAdapter {

        private final List<Fragment> fragments = new ArrayList<>();

        OnboardingAdapter(FragmentActivity fa) {
            super(fa);
            fragments.add(new Onboarding1Fragment());
            fragments.add(new Onboarding2Fragment());
            fragments.add(new Onboarding3Fragment());
        }

        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}
