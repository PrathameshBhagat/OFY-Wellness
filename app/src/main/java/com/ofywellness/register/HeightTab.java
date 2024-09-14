package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

// Tab for getting user's height
public class HeightTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_height, container, false);

        // Now we create number pickers for all decimal places of user's height
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
        view.findViewById(R.id.register_next_height_button).setOnClickListener((v) -> {

            // Try to parse the height as integer and set users height
            try {
                // Get a temporary variable for setting height
                int height;

                // Calculate height from the current value (position) of number pickers
                height = numberPicker1.getValue() * 100 + numberPicker2.getValue() * 10 + numberPicker3.getValue();

                // Set users height
                RegisterActivity.USER_HEIGHT = height;

            } catch (Exception e) {
                // If found exception make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Please enter your height", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if height is in range
            if (RegisterActivity.USER_HEIGHT < 60 || RegisterActivity.USER_HEIGHT > 300) {
                // If height is out of range make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Height should be between 60 and 300 cm", Toast.LENGTH_SHORT).show();
                return;
            }

            // If height is appropriate and valid move to next tab
            RegisterActivity.incrementTab();

        });

        return view;
    }

    // Method to be called when this tab resumes
    @Override
    public void onResume() {
        super.onResume();
        // Set label to ask user to enter his height
        ((RegisterActivity) requireActivity()).setLabel("Please enter your Height\n(in cm)");
    }
}
