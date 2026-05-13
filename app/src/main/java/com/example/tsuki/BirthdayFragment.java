package com.example.tsuki;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

public class BirthdayFragment extends Fragment {

    public static final String ARG_NAME = "arg_name";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_birthday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText inputBirthday = view.findViewById(R.id.inputBirthday);
        AppCompatButton btnContinue = view.findViewById(R.id.btnContinue);

        // Update header step indicator
        if (getActivity() instanceof ProfileSetupActivity) {
            ((ProfileSetupActivity) getActivity()).setStep(2);
        }

        // Open DatePickerDialog when field is tapped
        inputBirthday.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(
                    requireContext(),
                    (picker, year, month, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(),
                                "%02d / %02d / %d", dayOfMonth, month + 1, year);
                        inputBirthday.setText(date);
                        btnContinue.setEnabled(true);
                        btnContinue.setBackgroundResource(R.drawable.bg_signup_button);
                    },
                    cal.get(Calendar.YEAR) - 18,  // default: 18 years ago
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // TODO: navigate to next profile setup step
        btnContinue.setOnClickListener(v -> {
            // placeholder for next step navigation
        });
    }
}
