package com.example.tsuki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditPeriodBottomSheet extends BottomSheetDialogFragment {

    public interface OnPeriodSavedListener {
        void onPeriodSaved();
    }

    private OnPeriodSavedListener listener;

    private Calendar displayedMonth;
    private Calendar selectedStart = null;
    private Calendar selectedEnd   = null;

    // Adapter khusus untuk dialog — mendukung range selection
    private EditPeriodCalendarAdapter adapter;
    private TextView tvMonthYear;
    private TextView tvSelectedRange;

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("MMM d", Locale.getDefault());

    public void setOnPeriodSavedListener(OnPeriodSavedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_period, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayedMonth = Calendar.getInstance();
        tvMonthYear    = view.findViewById(R.id.tvDialogMonthYear);
        tvSelectedRange = view.findViewById(R.id.tvSelectedRange);

        setupCalendar(view);
        setupNavButtons(view);
        setupActionButtons(view);
        updateMonthLabel();
    }

    // ─── Calendar grid ────────────────────────────────────────────────────────

    private void setupCalendar(View view) {
        RecyclerView rv = view.findViewById(R.id.rvDialogCalendar);
        adapter = new EditPeriodCalendarAdapter(generateDays(), (day, cal) -> {
            if (!day.isCurrentMonth) return;

            if (selectedStart == null) {
                // Pilih tanggal mulai
                selectedStart = cal;
                selectedEnd   = null;
            } else if (selectedEnd == null && !cal.before(selectedStart)) {
                // Pilih tanggal akhir (harus >= start)
                selectedEnd = cal;
            } else {
                // Reset dan mulai ulang
                selectedStart = cal;
                selectedEnd   = null;
            }

            updateRangeLabel();
            adapter.setDays(generateDays());
        });

        rv.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rv.setAdapter(adapter);
    }

    private void setupNavButtons(View view) {
        ImageButton btnPrev = view.findViewById(R.id.btnDialogPrevMonth);
        ImageButton btnNext = view.findViewById(R.id.btnDialogNextMonth);

        btnPrev.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, -1);
            updateMonthLabel();
            adapter.setDays(generateDays());
        });

        btnNext.setOnClickListener(v -> {
            displayedMonth.add(Calendar.MONTH, 1);
            updateMonthLabel();
            adapter.setDays(generateDays());
        });
    }

    private void setupActionButtons(View view) {
        view.findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnDialogSave).setOnClickListener(v -> {
            if (selectedStart == null) return;
            if (selectedEnd == null) selectedEnd = (Calendar) selectedStart.clone();

            // Hitung panjang period dalam hari
            long diffMs = selectedEnd.getTimeInMillis() - selectedStart.getTimeInMillis();
            int periodLength = (int) (diffMs / (1000 * 60 * 60 * 24)) + 1;

            // Simpan ke SharedPreferences
            SharedPreferences prefs = requireContext()
                    .getSharedPreferences("cycle_data", Context.MODE_PRIVATE);
            prefs.edit()
                    .putInt("period_start_day",   selectedStart.get(Calendar.DAY_OF_MONTH))
                    .putInt("period_start_month", selectedStart.get(Calendar.MONTH))
                    .putInt("period_start_year",  selectedStart.get(Calendar.YEAR))
                    .putInt("period_length",       periodLength)
                    .apply();

            // Simpan ke Firestore
            new FirestoreManager().saveCycleData(
                    selectedStart.get(Calendar.DAY_OF_MONTH),
                    selectedStart.get(Calendar.MONTH),
                    selectedStart.get(Calendar.YEAR),
                    periodLength,
                    prefs.getInt("cycle_length", 28),
                    null, null);

            // Schedule reminder notifikasi
            ReminderScheduler.scheduleReminders(
                    requireContext(),
                    selectedStart.get(Calendar.DAY_OF_MONTH),
                    selectedStart.get(Calendar.MONTH),
                    selectedStart.get(Calendar.YEAR),
                    prefs.getInt("cycle_length", 28));

            if (listener != null) listener.onPeriodSaved();
            dismiss();
        });
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void updateMonthLabel() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(fmt.format(displayedMonth.getTime()));
    }

    private void updateRangeLabel() {
        if (selectedStart == null) {
            tvSelectedRange.setText("Tap a start date");
        } else if (selectedEnd == null) {
            tvSelectedRange.setText(DATE_FMT.format(selectedStart.getTime()) + "  →  ?");
        } else {
            tvSelectedRange.setText(
                    DATE_FMT.format(selectedStart.getTime())
                    + "  →  "
                    + DATE_FMT.format(selectedEnd.getTime()));
        }
    }

    private List<EditPeriodCalendarAdapter.EditDay> generateDays() {
        List<EditPeriodCalendarAdapter.EditDay> days = new ArrayList<>();

        Calendar cal = (Calendar) displayedMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int maxDay     = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startOffset = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Isi sel kosong bulan sebelumnya
        Calendar prevCal = (Calendar) cal.clone();
        prevCal.add(Calendar.MONTH, -1);
        int prevMax = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = startOffset - 1; i >= 0; i--) {
            Calendar c = (Calendar) prevCal.clone();
            c.set(Calendar.DAY_OF_MONTH, prevMax - i);
            days.add(new EditPeriodCalendarAdapter.EditDay(prevMax - i, false, c,
                    false, false));
        }

        // Isi tanggal bulan ini
        for (int d = 1; d <= maxDay; d++) {
            Calendar c = (Calendar) cal.clone();
            c.set(Calendar.DAY_OF_MONTH, d);

            boolean isInRange = false;
            boolean isEndpoint = false;

            if (selectedStart != null) {
                Calendar end = selectedEnd != null ? selectedEnd : selectedStart;
                isInRange  = !c.before(selectedStart) && !c.after(end);
                isEndpoint = isSameDay(c, selectedStart)
                        || (selectedEnd != null && isSameDay(c, selectedEnd));
            }

            days.add(new EditPeriodCalendarAdapter.EditDay(d, true, c,
                    isInRange, isEndpoint));
        }

        // Isi sisa sel bulan depan
        int remaining = 7 - (days.size() % 7);
        if (remaining < 7) {
            Calendar nextCal = (Calendar) cal.clone();
            nextCal.add(Calendar.MONTH, 1);
            for (int d = 1; d <= remaining; d++) {
                Calendar c = (Calendar) nextCal.clone();
                c.set(Calendar.DAY_OF_MONTH, d);
                days.add(new EditPeriodCalendarAdapter.EditDay(d, false, c,
                        false, false));
            }
        }

        return days;
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR)         == b.get(Calendar.YEAR)
                && a.get(Calendar.MONTH)    == b.get(Calendar.MONTH)
                && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
    }
}
