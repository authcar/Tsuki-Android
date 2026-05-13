package com.example.tsuki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class PeriodCalendarFragment extends Fragment {

    // Tanggal yang dipilih user (default: hari ini)
    private long selectedDateMillis = System.currentTimeMillis();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_period_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update header step indicator
        if (getActivity() instanceof ProfileSetupActivity) {
            ((ProfileSetupActivity) getActivity()).setStep(4);
        }

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        AppCompatButton btnContinue = view.findViewById(R.id.btnContinue);

        // Aktifkan tombol sejak awal karena kalender selalu punya tanggal terpilih
        btnContinue.setEnabled(true);
        btnContinue.setBackgroundResource(R.drawable.bg_signup_button);

        // Simpan tanggal setiap kali user memilih
        calendarView.setOnDateChangeListener((cv, year, month, dayOfMonth) -> {
            // month dari CalendarView adalah 0-based, +1 untuk tampilan
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedDateMillis = cal.getTimeInMillis();
        });

        btnContinue.setOnClickListener(v -> {
            // TODO: simpan selectedDateMillis, navigate ke fragment berikutnya
        });
    }
}
