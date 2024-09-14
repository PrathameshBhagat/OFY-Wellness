package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

// Tab for getting user's weight
public class WeightTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_weight, container, false);

        // Now we create number pickers for all decimal places of user's weight
        // Number picker for hundredth place
        NumberPicker numberPicker1 = view.findViewById(R.id.register_number_picker_1);

        // Number picker for tenth place
        NumberPicker numberPicker2 = view.findViewById(R.id.register_number_picker_2);

        // Number picker for one's place
        NumberPicker numberPicker3 = view.findViewById(R.id.register_number_picker_3);

        // Set the max values for each pickers accordingly
        numberPicker1.setMaxValue(2);
        numberPicker2.setMaxValue(9);
        numberPicker3.setMaxValue(9);

        // Set onclick listener to the next button
        view.findViewById(R.id.register_next_weight_button).setOnClickListener((v) -> {

            // Try to parse the weight as integer and set users weight
            try {
                // Get a temporary variable for setting weight
                int weight;

                // Calculate weight from the current value (position) of number pickers
                weight = numberPicker1.getValue() * 100 + numberPicker2.getValue() * 10 + numberPicker3.getValue();

                // Set users weight
                RegisterActivity.USER_WEIGHT = weight;

            } catch (Exception e) {
                // If found exception make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Please enter your weight", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if weight is in range
            if (RegisterActivity.USER_WEIGHT < 20 || RegisterActivity.USER_WEIGHT > 300) {
                // If weight is out of range make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Weight should be between 20 and 300 Kg", Toast.LENGTH_SHORT).show();
                return;
            }

            // If weight is appropriate and valid move to next tab
            RegisterActivity.incrementTab();

        });

        return view;
    }

    // Method to be called when this tab resumes
    @Override
    public void onResume() {
        super.onResume();
        // Set label to ask user to enter his weight
        ((RegisterActivity) requireActivity()).setLabel("Please enter your Weight\n(in Kg)");
    }
}
