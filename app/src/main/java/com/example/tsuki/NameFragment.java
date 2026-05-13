package com.example.tsuki;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class NameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update header step indicator
        if (getActivity() instanceof ProfileSetupActivity) {
            ((ProfileSetupActivity) getActivity()).setStep(1);
        }

        EditText inputName = view.findViewById(R.id.inputName);
        AppCompatButton btnContinue = view.findViewById(R.id.btnContinue);

        // Enable button only when name is filled
        inputName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                boolean filled = s != null && !s.toString().trim().isEmpty();
                btnContinue.setEnabled(filled);
                btnContinue.setBackgroundResource(filled
                        ? R.drawable.bg_signup_button
                        : R.drawable.bg_signup_button_disabled);
            }
        });

        // Navigate to BirthdayFragment
        btnContinue.setOnClickListener(v -> {
            String name = inputName.getText() != null
                    ? inputName.getText().toString().trim()
                    : "";

            BirthdayFragment birthdayFragment = new BirthdayFragment();
            Bundle args = new Bundle();
            args.putString(BirthdayFragment.ARG_NAME, name);
            birthdayFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter: fragment baru masuk dari kanan
                            R.anim.slide_out_left,  // exit: fragment lama keluar ke kiri
                            R.anim.slide_in_left,   // popEnter: saat back, masuk dari kiri
                            R.anim.slide_out_right) // popExit: saat back, keluar ke kanan
                    .replace(R.id.fragmentContainer, birthdayFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
