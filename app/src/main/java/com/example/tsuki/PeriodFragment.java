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

        // Navigate ke PeriodCalendarFragment
        btnContinue.setOnClickListener(v -> {
            int selectedDays = numberPicker.getValue();

            // Simpan period_length ke SharedPreferences dulu
            requireContext().getSharedPreferences("cycle_data", android.content.Context.MODE_PRIVATE)
                    .edit()
                    .putInt("period_length", selectedDays)
                    .apply();

            PeriodCalendarFragment nextFragment = new PeriodCalendarFragment();
            Bundle args = new Bundle();
            args.putInt("period_length", selectedDays);
            nextFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, nextFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
