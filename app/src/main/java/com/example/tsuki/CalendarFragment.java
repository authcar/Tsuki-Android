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
        setupMenuButton(view);

        // Render bulan saat ini
        renderMonth();
    }

    private void setupMenuButton(View view) {
        view.findViewById(R.id.btnMenuCalendar).setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), NotificationActivity.class)));
    }

    // ─── Setup RecyclerView grid ───────────────────────────────────────────────

    private void setupCalendarGrid(View view) {
        RecyclerView rvCalendar = view.findViewById(R.id.rvCalendar);
        calendarAdapter = new CalendarAdapter(new ArrayList<>());
        rvCalendar.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rvCalendar.setAdapter(calendarAdapter);

        calendarAdapter.setOnDayClickListener((day, position) -> {
            if (!day.isCurrentMonth()) return;

            // Buat tanggal dari CalendarDay
            Calendar selected = (Calendar) displayedMonth.clone();
            selected.set(Calendar.DAY_OF_MONTH, day.getDay());

            showLogDetailForDate(view, selected);
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

        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int year   = cal.get(Calendar.YEAR);
        int month  = cal.get(Calendar.MONTH);
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startOffset = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Isi sel kosong bulan sebelumnya
        Calendar prevCal = (Calendar) cal.clone();
        prevCal.add(Calendar.MONTH, -1);
        int prevMaxDay = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = startOffset - 1; i >= 0; i--) {
            days.add(new CalendarDay(prevMaxDay - i, false));
        }

        // Baca data dari SharedPreferences
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("cycle_data", Context.MODE_PRIVATE);
        int periodStartDay   = prefs.getInt("period_start_day",   -1);
        int periodStartMonth = prefs.getInt("period_start_month", -1);
        int periodStartYear  = prefs.getInt("period_start_year",  -1);
        int periodLength     = prefs.getInt("period_length",       5);
        int cycleLength      = prefs.getInt("cycle_length",       28);

        // Hitung tanggal-tanggal penting berdasarkan period start + cycle length
        // Ovulation terjadi cycleLength - 14 hari setelah period start
        // Fertile window: ovulation - 5 s/d ovulation + 1
        // Next period: period start + cycleLength

        Calendar periodStartCal = null;
        Calendar nextPeriodStart = null;
        Calendar ovulationDay    = null;
        Calendar fertileStart    = null;
        Calendar fertileEnd      = null;

        if (periodStartDay > 0) {
            periodStartCal = Calendar.getInstance();
            periodStartCal.set(periodStartYear, periodStartMonth, periodStartDay, 0, 0, 0);
            periodStartCal.set(Calendar.MILLISECOND, 0);

            // Next period = period start + cycleLength hari
            nextPeriodStart = (Calendar) periodStartCal.clone();
            nextPeriodStart.add(Calendar.DAY_OF_YEAR, cycleLength);

            // Ovulation = period start + (cycleLength - 14) hari
            ovulationDay = (Calendar) periodStartCal.clone();
            ovulationDay.add(Calendar.DAY_OF_YEAR, cycleLength - 14);

            // Fertile window = ovulation - 5 s/d ovulation + 1
            fertileStart = (Calendar) ovulationDay.clone();
            fertileStart.add(Calendar.DAY_OF_YEAR, -5);
            fertileEnd = (Calendar) ovulationDay.clone();
            fertileEnd.add(Calendar.DAY_OF_YEAR, 1);
        }

        Calendar today = Calendar.getInstance();

        for (int d = 1; d <= maxDay; d++) {
            CalendarDay day = new CalendarDay(d, true);

            Calendar thisDay = Calendar.getInstance();
            thisDay.set(year, month, d, 0, 0, 0);
            thisDay.set(Calendar.MILLISECOND, 0);

            // Tandai hari ini
            if (isSameDay(thisDay, today)) {
                day.setType(CalendarDay.DayType.TODAY);
            }

            if (periodStartCal != null) {
                // Tandai period (period start s/d period start + periodLength)
                Calendar periodEnd = (Calendar) periodStartCal.clone();
                periodEnd.add(Calendar.DAY_OF_YEAR, periodLength - 1);

                if (!thisDay.before(periodStartCal) && !thisDay.after(periodEnd)) {
                    if (day.getType() != CalendarDay.DayType.TODAY) {
                        day.setType(CalendarDay.DayType.PERIOD);
                    }
                    day.setLogged(true);
                }
                // Tandai next period (siklus berikutnya)
                else if (nextPeriodStart != null) {
                    Calendar nextPeriodEnd = (Calendar) nextPeriodStart.clone();
                    nextPeriodEnd.add(Calendar.DAY_OF_YEAR, periodLength - 1);
                    if (!thisDay.before(nextPeriodStart) && !thisDay.after(nextPeriodEnd)) {
                        if (day.getType() != CalendarDay.DayType.TODAY) {
                            day.setType(CalendarDay.DayType.PERIOD);
                        }
                    }
                }

                // Tandai ovulation
                if (ovulationDay != null && isSameDay(thisDay, ovulationDay)) {
                    if (day.getType() != CalendarDay.DayType.TODAY
                            && day.getType() != CalendarDay.DayType.PERIOD) {
                        day.setType(CalendarDay.DayType.OVULATION);
                    }
                }

                // Tandai fertile window
                if (fertileStart != null && fertileEnd != null
                        && !thisDay.before(fertileStart) && !thisDay.after(fertileEnd)) {
                    if (day.getType() == CalendarDay.DayType.NORMAL) {
                        day.setType(CalendarDay.DayType.FERTILE);
                    }
                }
            }

            days.add(day);
        }

        // Isi sisa sel bulan depan
        int remaining = 7 - (days.size() % 7);
        if (remaining < 7) {
            for (int d = 1; d <= remaining; d++) {
                days.add(new CalendarDay(d, false));
            }
        }

        return days;
    }

    // ─── Log detail card ──────────────────────────────────────────────────────

    private void showLogDetailForDate(View view, Calendar date) {
        android.view.View logDetailCard = view.findViewById(R.id.logDetailCard);
        android.widget.TextView tvDate     = view.findViewById(R.id.tvLogDetailDate);
        android.widget.TextView tvFlow     = view.findViewById(R.id.tvLogDetailFlow);
        android.widget.TextView tvSymptoms = view.findViewById(R.id.tvLogDetailSymptoms);
        android.widget.TextView tvMood     = view.findViewById(R.id.tvLogDetailMood);
        android.widget.TextView tvEmpty    = view.findViewById(R.id.tvLogDetailEmpty);
        android.view.View rowFlow     = view.findViewById(R.id.rowFlow);
        android.view.View rowSymptoms = view.findViewById(R.id.rowSymptoms);
        android.view.View rowMood     = view.findViewById(R.id.rowMood);

        // Format tanggal untuk header card
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("EEEE, MMM d yyyy", java.util.Locale.getDefault());
        tvDate.setText(fmt.format(date.getTime()));

        // Tampilkan card dulu (dengan loading state)
        logDetailCard.setVisibility(android.view.View.VISIBLE);
        rowFlow.setVisibility(android.view.View.GONE);
        rowSymptoms.setVisibility(android.view.View.GONE);
        rowMood.setVisibility(android.view.View.GONE);
        tvEmpty.setVisibility(android.view.View.GONE);

        // Tombol tutup
        view.findViewById(R.id.btnCloseLogDetail).setOnClickListener(v ->
                logDetailCard.setVisibility(android.view.View.GONE));

        // Load dari Firestore
        String dateKey = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(date.getTime());

        new FirestoreManager().getLog(dateKey, data -> {
            if (!isAdded()) return;

            if (data == null) {
                tvEmpty.setVisibility(android.view.View.VISIBLE);
                return;
            }

            boolean hasData = false;

            // Flow
            String flow = (String) data.get("flow");
            if (flow != null && !flow.isEmpty()) {
                rowFlow.setVisibility(android.view.View.VISIBLE);
                tvFlow.setText(flow);
                hasData = true;
            }

            // Symptoms
            Object symptomsObj = data.get("symptoms");
            if (symptomsObj instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) symptomsObj;
                if (!list.isEmpty()) {
                    rowSymptoms.setVisibility(android.view.View.VISIBLE);
                    tvSymptoms.setText(android.text.TextUtils.join(", ", list));
                    hasData = true;
                }
            }

            // Moods
            Object moodsObj = data.get("moods");
            if (moodsObj instanceof java.util.List) {
                java.util.List<?> list = (java.util.List<?>) moodsObj;
                if (!list.isEmpty()) {
                    rowMood.setVisibility(android.view.View.VISIBLE);
                    tvMood.setText(android.text.TextUtils.join(", ", list));
                    hasData = true;
                }
            }

            if (!hasData) {
                tvEmpty.setVisibility(android.view.View.VISIBLE);
            }

        }, e -> {
            if (isAdded()) tvEmpty.setVisibility(android.view.View.VISIBLE);
        });
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR)  == b.get(Calendar.YEAR)
            && a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
            && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
    }
}
