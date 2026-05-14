package com.example.tsuki;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoadingFragment extends Fragment {

    private static final int DURATION_MS = 3000; // total durasi animasi (ms)
    private static final int INTERVAL_MS  = 30;  // update setiap 30ms

    private final Handler handler = new Handler(Looper.getMainLooper());
    private CircularProgressView circularProgress;
    private float currentProgress = 0f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sembunyikan header back button & step saat loading
        if (getActivity() instanceof ProfileSetupActivity) {
            ((ProfileSetupActivity) getActivity()).setHeaderVisible(false);
        }

        circularProgress = view.findViewById(R.id.circularProgress);
        startProgress();
    }

    private void startProgress() {
        float increment = 100f / (DURATION_MS / (float) INTERVAL_MS);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) return;

                currentProgress += increment;
                if (currentProgress >= 100f) {
                    currentProgress = 100f;
                    circularProgress.setProgress(currentProgress);

                    // Delay sebentar di 100% agar user sempat melihat, lalu ke NotificationActivity
                    handler.postDelayed(() -> {
                        if (!isAdded()) return;
                        android.content.Intent intent = new android.content.Intent(
                                requireContext(), NotificationActivity.class);
                        // Hapus semua back stack agar user tidak bisa back ke onboarding
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }, 600);
                    return;
                }

                circularProgress.setProgress(currentProgress);
                handler.postDelayed(this, INTERVAL_MS);
            }
        };

        handler.postDelayed(runnable, INTERVAL_MS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);

        // Tampilkan kembali header saat fragment ini ditinggalkan
        if (getActivity() instanceof ProfileSetupActivity) {
            ((ProfileSetupActivity) getActivity()).setHeaderVisible(true);
        }
    }
}
