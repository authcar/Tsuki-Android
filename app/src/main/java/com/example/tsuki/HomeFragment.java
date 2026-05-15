package com.example.tsuki;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int DAYS_BEFORE = 30;
    private static final int DAYS_AFTER  = 60;
    private static final int TODAY_INDEX  = DAYS_BEFORE;

    private TextView tvGreeting;
    private TextView tvPhase;
    private TextView tvDayCount;
    private TextView tvDaysLeft;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGreeting  = view.findViewById(R.id.tvGreeting);
        tvPhase     = view.findViewById(R.id.tvPhase);
        tvDayCount  = view.findViewById(R.id.tvDayCount);
        tvDaysLeft  = view.findViewById(R.id.tvDaysLeft);

        setupGreeting();
        setupWeekStrip(view);
        setupMenuButton(view);
        setupUserName(view);

        // Tampilkan info untuk hari ini saat pertama kali
        updateCycleInfo(Calendar.getInstance());
        updatePredictionCards(view);
    }

    private void setupUserName(View view) {
        android.widget.TextView tvName = view.findViewById(R.id.tvName);
        String name = requireContext()
                .getSharedPreferences("user_data", Context.MODE_PRIVATE)
                .getString("user_name", "");
        if (!name.isEmpty()) {
            String display = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            tvName.setText(display);
        }
    }

    private void setupMenuButton(View view) {
        view.findViewById(R.id.btnMenu).setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), NotificationActivity.class)));
    }

    // ─── Prediction cards ─────────────────────────────────────────────────────

    private void updatePredictionCards(View view) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("cycle_data", Context.MODE_PRIVATE);

        int startDay   = prefs.getInt("period_start_day",   -1);
        int startMonth = prefs.getInt("period_start_month", -1);
        int startYear  = prefs.getInt("period_start_year",  -1);
        int cycleLen   = prefs.getInt("cycle_length",       28);

        TextView tvNextPeriod  = view.findViewById(R.id.tvNextPeriodDate);
        TextView tvOvulation   = view.findViewById(R.id.tvOvulationDate);
        TextView tvFertile     = view.findViewById(R.id.tvFertileDate);

        if (startDay == -1) {
            tvNextPeriod.setText("--");
            tvOvulation.setText("--");
            tvFertile.setText("-- - --");
            return;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("MMM d", Locale.getDefault());

        // Next period = period start + cycleLength
        Calendar nextPeriod = Calendar.getInstance();
        nextPeriod.set(startYear, startMonth, startDay);
        nextPeriod.add(Calendar.DAY_OF_YEAR, cycleLen);
        tvNextPeriod.setText(fmt.format(nextPeriod.getTime()));

        // Ovulation = period start + (cycleLength - 14)
        Calendar ovulation = Calendar.getInstance();
        ovulation.set(startYear, startMonth, startDay);
        ovulation.add(Calendar.DAY_OF_YEAR, cycleLen - 14);
        tvOvulation.setText(fmt.format(ovulation.getTime()));

        // Fertile window = ovulation - 5 s/d ovulation + 1
        Calendar fertileStart = (Calendar) ovulation.clone();
        fertileStart.add(Calendar.DAY_OF_YEAR, -5);
        Calendar fertileEnd = (Calendar) ovulation.clone();
        fertileEnd.add(Calendar.DAY_OF_YEAR, 1);
        tvFertile.setText(fmt.format(fertileStart.getTime())
                + "  -  "
                + fmt.format(fertileEnd.getTime()));
    }

    // ─── Greeting dinamis ─────────────────────────────────────────────────────

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning,";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon,";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Good Evening,";
        } else {
            greeting = "Good Night,";
        }
        tvGreeting.setText(greeting);
    }

    // ─── Week strip ───────────────────────────────────────────────────────────

    private void setupWeekStrip(View view) {
        RecyclerView rvWeekStrip = view.findViewById(R.id.rvWeekStrip);

        List<Calendar> days = new ArrayList<>();
        Calendar base = Calendar.getInstance();
        base.add(Calendar.DAY_OF_YEAR, -DAYS_BEFORE);

        for (int i = 0; i < DAYS_BEFORE + DAYS_AFTER + 1; i++) {
            Calendar day = Calendar.getInstance();
            day.setTimeInMillis(base.getTimeInMillis());
            days.add(day);
            base.add(Calendar.DAY_OF_YEAR, 1);
        }

        WeekDayAdapter adapter = new WeekDayAdapter(days, TODAY_INDEX);

        // Saat user tap hari → update cycle info card
        adapter.setOnDayClickListener((day, position) -> updateCycleInfo(day));

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvWeekStrip.setLayoutManager(layoutManager);
        rvWeekStrip.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvWeekStrip);

        rvWeekStrip.scrollToPosition(TODAY_INDEX);
        rvWeekStrip.post(() -> {
            LinearLayoutManager lm = (LinearLayoutManager) rvWeekStrip.getLayoutManager();
            if (lm != null) {
                lm.scrollToPositionWithOffset(TODAY_INDEX,
                        rvWeekStrip.getWidth() / 2 - 24);
            }
        });
    }

    // ─── Update cycle info card ───────────────────────────────────────────────

    private void updateCycleInfo(Calendar selectedDate) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("cycle_data", Context.MODE_PRIVATE);

        int startDay   = prefs.getInt("period_start_day",   -1);
        int startMonth = prefs.getInt("period_start_month", -1);
        int startYear  = prefs.getInt("period_start_year",  -1);
        int periodLen  = prefs.getInt("period_length",       5);
        int cycleLen   = prefs.getInt("cycle_length",       28);

        if (startDay == -1) {
            // Data belum ada — tampilkan placeholder
            tvPhase.setText("Set up your cycle");
            tvDayCount.setText("--");
            tvDaysLeft.setText("No data yet");
            return;
        }

        Calendar periodStart = Calendar.getInstance();
        periodStart.set(startYear, startMonth, startDay);

        CycleCalculator.CycleInfo info = CycleCalculator.calculate(
                selectedDate, periodStart, periodLen, cycleLen);

        tvPhase.setText(info.phaseName);
        tvDayCount.setText("Day " + info.dayInPhase); // ← pakai dayInPhase, bukan cycleDay

        String daysLeftText;
        if (info.daysLeftInPhase == 0) {
            daysLeftText = "Last day";
        } else {
            daysLeftText = info.daysLeftInPhase + " days left";
        }
        tvDaysLeft.setText(daysLeftText);
    }
}
