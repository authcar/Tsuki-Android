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
import java.util.Map;

public class LogFragment extends Fragment {

    private Calendar selectedDate = Calendar.getInstance();
    private TextView tvLogDate;
    private View rootView;

    // Track chip selection state
    private String selectedFlow = null;
    private final List<String> selectedSymptoms = new ArrayList<>();
    private final List<String> selectedMoods    = new ArrayList<>();

    private static final SimpleDateFormat DATE_KEY_FMT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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
        rootView = view;

        tvLogDate = view.findViewById(R.id.tvLogDate);
        updateDateLabel();

        // Menu button
        view.findViewById(R.id.btnMenuLog).setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), NotificationActivity.class)));

        // Date picker — saat tanggal berubah, load data log untuk tanggal baru
        view.findViewById(R.id.datePickerCard).setOnClickListener(v -> {
            new DatePickerDialog(requireContext(),
                    (picker, year, month, day) -> {
                        selectedDate.set(year, month, day);
                        updateDateLabel();
                        loadLogForDate(); // ← load data setelah tanggal berubah
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Setup chip listeners
        setupFlowChip(view, R.id.chipFlowLow,    "Low");
        setupFlowChip(view, R.id.chipFlowNormal, "Normal");
        setupFlowChip(view, R.id.chipFlowHigh,   "High");

        setupToggleChip(view, R.id.chipCramps,     "Cramps",      selectedSymptoms);
        setupToggleChip(view, R.id.chipHeadaches,  "Headaches",   selectedSymptoms);
        setupToggleChip(view, R.id.chipWeightGain, "Weight gain", selectedSymptoms);
        setupToggleChip(view, R.id.chipBloating,   "Bloating",    selectedSymptoms);
        setupToggleChip(view, R.id.chipAcne,       "Acne",        selectedSymptoms);
        setupToggleChip(view, R.id.chipCravings,   "Cravings",    selectedSymptoms);
        setupToggleChip(view, R.id.chipBackPain,   "Back Pain",   selectedSymptoms);
        setupToggleChip(view, R.id.chipMoodSwings, "Mood swings", selectedSymptoms);

        setupToggleChip(view, R.id.chipHappy,       "Happy",       selectedMoods);
        setupToggleChip(view, R.id.chipCalm,        "Calm",        selectedMoods);
        setupToggleChip(view, R.id.chipSad,         "Sad",         selectedMoods);
        setupToggleChip(view, R.id.chipAnxious,     "Anxious",     selectedMoods);
        setupToggleChip(view, R.id.chipLowEnergy,   "Low Energy",  selectedMoods);
        setupToggleChip(view, R.id.chipCranky,      "Cranky",      selectedMoods);
        setupToggleChip(view, R.id.chipSensitivity, "Sensitivity", selectedMoods);

        // Save button
        view.findViewById(R.id.btnSaveLog).setOnClickListener(v -> saveLog());

        // Load data untuk hari ini saat pertama kali dibuka
        loadLogForDate();
    }

    private void updateDateLabel() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        tvLogDate.setText(fmt.format(selectedDate.getTime()));
    }

    // ─── Load log dari Firestore dan pre-fill chip ────────────────────────────

    private void loadLogForDate() {
        if (!isAdded()) return;
        String dateKey = DATE_KEY_FMT.format(selectedDate.getTime());

        new FirestoreManager().getLog(dateKey, data -> {
            if (!isAdded()) return;

            // Reset semua chip ke state default dulu
            resetAllChips();

            if (data == null) return; // tidak ada log untuk tanggal ini

            // Pre-fill flow
            String flow = (String) data.get("flow");
            if (flow != null && !flow.isEmpty()) {
                selectedFlow = flow;
                applyFlowSelection(flow);
            }

            // Pre-fill symptoms
            Object symptomsObj = data.get("symptoms");
            if (symptomsObj instanceof List) {
                List<?> symptoms = (List<?>) symptomsObj;
                for (Object s : symptoms) {
                    if (s instanceof String) {
                        String symptom = (String) s;
                        selectedSymptoms.add(symptom);
                        applyChipSelection(symptom, selectedSymptoms);
                    }
                }
            }

            // Pre-fill moods
            Object moodsObj = data.get("moods");
            if (moodsObj instanceof List) {
                List<?> moods = (List<?>) moodsObj;
                for (Object m : moods) {
                    if (m instanceof String) {
                        String mood = (String) m;
                        selectedMoods.add(mood);
                        applyChipSelection(mood, selectedMoods);
                    }
                }
            }

        }, null);
    }

    /** Reset semua chip ke background default dan clear state */
    private void resetAllChips() {
        selectedFlow = null;
        selectedSymptoms.clear();
        selectedMoods.clear();

        int[] allChips = {
            R.id.chipFlowLow, R.id.chipFlowNormal, R.id.chipFlowHigh,
            R.id.chipCramps, R.id.chipHeadaches, R.id.chipWeightGain,
            R.id.chipBloating, R.id.chipAcne, R.id.chipCravings,
            R.id.chipBackPain, R.id.chipMoodSwings,
            R.id.chipHappy, R.id.chipCalm, R.id.chipSad, R.id.chipAnxious,
            R.id.chipLowEnergy, R.id.chipCranky, R.id.chipSensitivity
        };
        for (int id : allChips) {
            View chip = rootView.findViewById(id);
            if (chip != null) chip.setBackgroundResource(R.drawable.bg_log_chip);
        }
    }

    /** Terapkan visual selected untuk flow chip */
    private void applyFlowSelection(String flow) {
        int chipId;
        switch (flow) {
            case "Low":    chipId = R.id.chipFlowLow;    break;
            case "Normal": chipId = R.id.chipFlowNormal; break;
            case "High":   chipId = R.id.chipFlowHigh;   break;
            default: return;
        }
        View chip = rootView.findViewById(chipId);
        if (chip != null) chip.setBackgroundResource(R.drawable.bg_log_chip_selected);
    }

    /** Terapkan visual selected untuk symptom/mood chip berdasarkan label teks */
    private void applyChipSelection(String value, List<String> list) {
        // Map value ke chip ID
        int chipId = getChipIdByValue(value);
        if (chipId == -1) return;
        View chip = rootView.findViewById(chipId);
        if (chip != null) chip.setBackgroundResource(R.drawable.bg_log_chip_selected);
    }

    private int getChipIdByValue(String value) {
        switch (value) {
            case "Cramps":      return R.id.chipCramps;
            case "Headaches":   return R.id.chipHeadaches;
            case "Weight gain": return R.id.chipWeightGain;
            case "Bloating":    return R.id.chipBloating;
            case "Acne":        return R.id.chipAcne;
            case "Cravings":    return R.id.chipCravings;
            case "Back Pain":   return R.id.chipBackPain;
            case "Mood swings": return R.id.chipMoodSwings;
            case "Happy":       return R.id.chipHappy;
            case "Calm":        return R.id.chipCalm;
            case "Sad":         return R.id.chipSad;
            case "Anxious":     return R.id.chipAnxious;
            case "Low Energy":  return R.id.chipLowEnergy;
            case "Cranky":      return R.id.chipCranky;
            case "Sensitivity": return R.id.chipSensitivity;
            default: return -1;
        }
    }

    // ─── Chip listeners ───────────────────────────────────────────────────────

    private void setupFlowChip(View root, int chipId, String value) {
        LinearLayout chip = root.findViewById(chipId);
        chip.setOnClickListener(v -> {
            root.findViewById(R.id.chipFlowLow).setBackgroundResource(R.drawable.bg_log_chip);
            root.findViewById(R.id.chipFlowNormal).setBackgroundResource(R.drawable.bg_log_chip);
            root.findViewById(R.id.chipFlowHigh).setBackgroundResource(R.drawable.bg_log_chip);

            if (value.equals(selectedFlow)) {
                selectedFlow = null;
            } else {
                selectedFlow = value;
                chip.setBackgroundResource(R.drawable.bg_log_chip_selected);
            }
        });
    }

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

    // ─── Save ─────────────────────────────────────────────────────────────────

    private void saveLog() {
        String dateKey = DATE_KEY_FMT.format(selectedDate.getTime());

        new FirestoreManager().saveLog(
                dateKey,
                selectedFlow,
                new ArrayList<>(selectedSymptoms),
                new ArrayList<>(selectedMoods),
                () -> showSavedDialog(),
                e -> android.widget.Toast.makeText(requireContext(),
                        "Failed to save: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show()
        );
    }

    private void showSavedDialog() {
        if (!isAdded()) return;

        // Inflate layout custom
        android.view.View dialogView = android.view.LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_log_saved, null);

        // Set teks tanggal
        android.widget.TextView tvDate = dialogView.findViewById(R.id.tvDialogDate);
        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat(
                "Log for EEEE, MMM d", java.util.Locale.getDefault());
        tvDate.setText(fmt.format(selectedDate.getTime()) + " has been saved.");

        // Buat dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(
                requireContext(), R.style.DialogRounded)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Tombol OK tutup dialog
        dialogView.findViewById(R.id.btnDialogOk).setOnClickListener(v -> dialog.dismiss());

        // Buat background dialog transparan agar rounded corner terlihat
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }
}
