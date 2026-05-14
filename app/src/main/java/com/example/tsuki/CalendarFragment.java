package com.example.tsuki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private Calendar displayedMonth;
    private CalendarAdapter calendarAdapter;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;

    private static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayedMonth = Calendar.getInstance();

        setupCalendarGrid(view);
        setupSpinners(view);
        setupNavButtons(view);
        setupEditPeriodButton(view);

        // Render bulan saat ini
        renderMonth();
    }

    // ─── Setup RecyclerView grid ───────────────────────────────────────────────

    private void setupCalendarGrid(View view) {
        RecyclerView rvCalendar = view.findViewById(R.id.rvCalendar);
        calendarAdapter = new CalendarAdapter(new ArrayList<>());
        rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rvCalendar.setAdapter(calendarAdapter);

        calendarAdapter.setOnDayClickListener((day, position) -> {
            // TODO: tampilkan detail atau log untuk tanggal ini
        });
    }

    // ─── Setup Spinner bulan & tahun ──────────────────────────────────────────

    private void setupSpinners(View view) {
        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerYear  = view.findViewById(R.id.spinnerYear);

        // Adapter bulan
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                MONTH_NAMES);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerMonth.setSelection(displayedMonth.get(Calendar.MONTH));

        // Adapter tahun: 10 tahun ke belakang s/d 5 tahun ke depan
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int y = currentYear - 10; y <= currentYear + 5; y++) {
            years.add(String.valueOf(y));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        spinnerYear.setSelection(years.indexOf(String.valueOf(currentYear)));

        // Listener — update kalender saat spinner berubah
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                displayedMonth.set(Calendar.MONTH, pos);
                renderMonth();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                displayedMonth.set(Calendar.YEAR, currentYear - 10 + pos);
                renderMonth();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ─── Tombol Edit Period Dates ─────────────────────────────────────────────

    private void setupEditPeriodButton(View view) {
        view.findViewById(R.id.editPeriodCard).setOnClickListener(v -> {
            EditPeriodBottomSheet sheet = new EditPeriodBottomSheet();
            sheet.setOnPeriodSavedListener(() -> renderMonth());
            sheet.show(getParentFragmentManager(), "edit_period");
        });
    }

    // ─── onResume: refresh kalender saat kembali ke tab ini ───────────────────

    @Override
    public void onResume() {
        super.onResume();
        renderMonth();
    }

    // ─── Tombol < > navigasi bulan ────────────────────────────────────────────

    private void setupNavButtons(View view) {
        ImageButton btnPrev = view.findViewById(R.id.btnPrevMonth);
        ImageButton btnNext = view.findViewById(R.id.btnNextMonth);

        btnPrev.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            syncSpinnersToMonth();
            renderMonth();
        });

        btnNext.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            syncSpinnersToMonth();
            renderMonth();
        });
    }

    private void syncSpinnersToMonth() {
        spinnerMonth.setSelection(displayedMonth.get(Calendar.MONTH));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int offset = displayedMonth.get(Calendar.YEAR) - (currentYear - 10);
        if (offset >= 0 && offset < spinnerYear.getCount()) {
            spinnerYear.setSelection(offset);
        }
    }

    // ─── Generate dan render grid kalender ────────────────────────────────────

    private void renderMonth() {
        List<CalendarDay> days = generateDays();
        calendarAdapter.setDays(days);
    }

    private List<CalendarDay> generateDays() {
        List<CalendarDay> days = new ArrayList<>();

        // Clone agar tidak mengubah displayedMonth
        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Hari pertama bulan ini jatuh di kolom ke berapa (0=Sun, 6=Sat)
        int startOffset = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Isi sel kosong dengan tanggal bulan sebelumnya
        Calendar prevCal = (Calendar) cal.clone();
        prevCal.add(Calendar.MONTH, -1);
        int prevMaxDay = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = startOffset - 1; i >= 0; i--) {
            days.add(new CalendarDay(prevMaxDay - i, false));
        }

        // Baca data period dari SharedPreferences
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("cycle_data", Context.MODE_PRIVATE);
        int periodStartDay   = prefs.getInt("period_start_day",   -1);
        int periodStartMonth = prefs.getInt("period_start_month", -1);
        int periodStartYear  = prefs.getInt("period_start_year",  -1);
        int periodLength     = prefs.getInt("period_length",       5);

        // Isi tanggal bulan ini
        Calendar today = Calendar.getInstance();
        for (int d = 1; d <= maxDay; d++) {
            CalendarDay day = new CalendarDay(d, true);

            // Tandai hari ini
            if (today.get(Calendar.YEAR) == year
                    && today.get(Calendar.MONTH) == month
                    && today.get(Calendar.DAY_OF_MONTH) == d) {
                day.setType(CalendarDay.DayType.TODAY);
            }

            // Tandai period dari data user
            if (periodStartDay > 0 && periodStartMonth == month && periodStartYear == year) {
                if (d >= periodStartDay && d < periodStartDay + periodLength) {
                    if (day.getType() != CalendarDay.DayType.TODAY) {
                        day.setType(CalendarDay.DayType.PERIOD);
                    }
                    day.setLogged(true);
                }
            }

            days.add(day);
        }

        // Isi sisa sel dengan tanggal bulan depan
        int remaining = 7 - (days.size() % 7);
        if (remaining < 7) {
            for (int d = 1; d <= remaining; d++) {
                days.add(new CalendarDay(d, false));
            }
        }

        return days;
    }
}
