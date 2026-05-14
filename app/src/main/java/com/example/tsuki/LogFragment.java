package com.example.tsuki;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LogFragment extends Fragment {

    private Calendar selectedDate = Calendar.getInstance();
    private TextView tvLogDate;

    // Track chip selection state
    private String selectedFlow = null;
    private final List<String> selectedSymptoms = new ArrayList<>();
    private final List<String> selectedMoods    = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLogDate = view.findViewById(R.id.tvLogDate);
        updateDateLabel();

        // Menu button
        view.findViewById(R.id.btnMenuLog).setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), NotificationActivity.class)));

        // Date picker
        view.findViewById(R.id.datePickerCard).setOnClickListener(v -> {
            new DatePickerDialog(requireContext(),
                    (picker, year, month, day) -> {
                        selectedDate.set(year, month, day);
                        updateDateLabel();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Flow chips — single select
        setupFlowChip(view, R.id.chipFlowLow,    "Low");
        setupFlowChip(view, R.id.chipFlowNormal, "Normal");
        setupFlowChip(view, R.id.chipFlowHigh,   "High");

        // Symptom chips — multi select
        setupToggleChip(view, R.id.chipCramps,     "Cramps",      selectedSymptoms);
        setupToggleChip(view, R.id.chipHeadaches,  "Headaches",   selectedSymptoms);
        setupToggleChip(view, R.id.chipWeightGain, "Weight gain", selectedSymptoms);
        setupToggleChip(view, R.id.chipBloating,   "Bloating",    selectedSymptoms);
        setupToggleChip(view, R.id.chipAcne,       "Acne",        selectedSymptoms);
        setupToggleChip(view, R.id.chipCravings,   "Cravings",    selectedSymptoms);
        setupToggleChip(view, R.id.chipBackPain,   "Back Pain",   selectedSymptoms);
        setupToggleChip(view, R.id.chipMoodSwings, "Mood swings", selectedSymptoms);

        // Mood chips — multi select
        setupToggleChip(view, R.id.chipHappy,       "Happy",       selectedMoods);
        setupToggleChip(view, R.id.chipCalm,        "Calm",        selectedMoods);
        setupToggleChip(view, R.id.chipSad,         "Sad",         selectedMoods);
        setupToggleChip(view, R.id.chipAnxious,     "Anxious",     selectedMoods);
        setupToggleChip(view, R.id.chipLowEnergy,   "Low Energy",  selectedMoods);
        setupToggleChip(view, R.id.chipCranky,      "Cranky",      selectedMoods);
        setupToggleChip(view, R.id.chipSensitivity, "Sensitivity", selectedMoods);

        // Save button
        view.findViewById(R.id.btnSaveLog).setOnClickListener(v -> saveLog());
    }

    private void updateDateLabel() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        tvLogDate.setText(fmt.format(selectedDate.getTime()));
    }

    /** Flow: single select — deselect yang lain */
    private void setupFlowChip(View root, int chipId, String value) {
        LinearLayout chip = root.findViewById(chipId);
        chip.setOnClickListener(v -> {
            // Deselect semua flow chip dulu
            root.findViewById(R.id.chipFlowLow).setBackgroundResource(R.drawable.bg_log_chip);
            root.findViewById(R.id.chipFlowNormal).setBackgroundResource(R.drawable.bg_log_chip);
            root.findViewById(R.id.chipFlowHigh).setBackgroundResource(R.drawable.bg_log_chip);

            if (value.equals(selectedFlow)) {
                // Tap lagi → deselect
                selectedFlow = null;
            } else {
                selectedFlow = value;
                chip.setBackgroundResource(R.drawable.bg_log_chip_selected);
            }
        });
    }

    /** Symptom/Mood: multi select toggle */
    private void setupToggleChip(View root, int chipId, String value, List<String> list) {
        LinearLayout chip = root.findViewById(chipId);
        chip.setOnClickListener(v -> {
            if (list.contains(value)) {
                list.remove(value);
                chip.setBackgroundResource(R.drawable.bg_log_chip);
            } else {
                list.add(value);
                chip.setBackgroundResource(R.drawable.bg_log_chip_selected);
            }
        });
    }

    private void saveLog() {
        // TODO: simpan ke database/SharedPreferences
        // Data yang tersedia:
        // - selectedDate
        // - selectedFlow (null jika tidak dipilih)
        // - selectedSymptoms (list)
        // - selectedMoods (list)

        android.widget.Toast.makeText(requireContext(),
                "Log saved!", android.widget.Toast.LENGTH_SHORT).show();
    }
}
