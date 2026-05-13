package com.example.tsuki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class PeriodFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_period, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update header step indicator
        if (getActivity() instanceof ProfileSetupActivity) {
            ((ProfileSetupActivity) getActivity()).setStep(3);
        }

        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
        AppCompatButton btnContinue = view.findViewById(R.id.btnContinue);

        // Rentang 1–10 hari, default 3
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(3);

        // Tombol Continue langsung aktif karena selalu ada nilai terpilih
        btnContinue.setOnClickListener(v -> {
            int selectedDays = numberPicker.getValue();

            // TODO: simpan selectedDays, lalu navigate ke fragment berikutnya
        });
    }
}
