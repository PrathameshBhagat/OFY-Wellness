package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

// Tab for getting user's age
public class AgeTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_age, container, false);

        // Now we create number pickers for all decimal places of user's age
        // Number picker for hundredth place
        NumberPicker numberPicker1 = view.findViewById(R.id.register_number_picker_1);

        // Number picker for tenth place
        NumberPicker numberPicker2 = view.findViewById(R.id.register_number_picker_2);

        // Number picker for one's place
        NumberPicker numberPicker3 = view.findViewById(R.id.register_number_picker_3);

        // Set the max values for each pickers accordingly
        numberPicker1.setMaxValue(1);
        numberPicker2.setMaxValue(9);
        numberPicker3.setMaxValue(9);

        // Set onclick listener to the button
        view.findViewById(R.id.register_next_age_button).setOnClickListener((v) -> {

            // Try to parse the age as integer and set users age
            try {
                // Get a temporary variable for setting age
                int age;

                // Calculate age from the current value (position) of number pickers
                age = numberPicker1.getValue() * 100 + numberPicker2.getValue() * 10 + numberPicker3.getValue();

                // Set users age
                RegisterActivity.USER_AGE = age;

            } catch (Exception e) {
                // If found exception make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Please enter your age", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if age is in range
            if (RegisterActivity.USER_AGE < 10 || RegisterActivity.USER_AGE > 150) {
                // If age is out of range make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Age should be between 10 and 150 years", Toast.LENGTH_SHORT).show();
                return;
            }

            // If age is appropriate and valid move to next tab
            RegisterActivity.incrementTab();

        });

        return view;
    }

    // Method to be called when this tab resumes
    @Override
    public void onResume() {
        super.onResume();
        // Set label to ask user to enter his age
        ((RegisterActivity) requireActivity()).setLabel("Please enter your Age\n(in years)");
    }
}
