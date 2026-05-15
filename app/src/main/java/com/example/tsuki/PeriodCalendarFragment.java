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
            java.util.Calendar selected = java.util.Calendar.getInstance();
            selected.setTimeInMillis(selectedDateMillis);

            int startDay   = selected.get(java.util.Calendar.DAY_OF_MONTH);
            int startMonth = selected.get(java.util.Calendar.MONTH);
            int startYear  = selected.get(java.util.Calendar.YEAR);

            // Ambil period_length yang dikirim dari PeriodFragment
            int periodLength = getArguments() != null
                    ? getArguments().getInt("period_length", 5) : 5;
            int cycleLength  = 28; // default, bisa ditambahkan input nanti

            // Simpan ke SharedPreferences (cache lokal)
            android.content.SharedPreferences prefs = requireContext()
                    .getSharedPreferences("cycle_data", android.content.Context.MODE_PRIVATE);
            prefs.edit()
                    .putInt("period_start_day",   startDay)
                    .putInt("period_start_month", startMonth)
                    .putInt("period_start_year",  startYear)
                    .putInt("period_length",       periodLength)
                    .putInt("cycle_length",        cycleLength)
                    .apply();

            // Simpan ke Firestore
            new FirestoreManager().saveCycleData(
                    startDay, startMonth, startYear,
                    periodLength, cycleLength,
                    null, null);

            // Navigate ke LoadingFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left)
                    .replace(R.id.fragmentContainer, new LoadingFragment())
                    .commit();
        });
    }
}
