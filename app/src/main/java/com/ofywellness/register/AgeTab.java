package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

public class AgeTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_age, container, false);

        // Set onclick listener to the button
        view.findViewById(R.id.register_next_age_button).setOnClickListener((v) -> {

            // Get the age provided by user
            String age = ((TextView) view.findViewById(R.id.register_age_field)).getText().toString();

            // Try to parse the age as integer and set users height
            try {
                // Set users height
                RegisterActivity.USER_AGE = Integer.parseInt(age);
            } catch (NumberFormatException e) {
                // If found exception make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Please enter your age", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if age is in range
            if( RegisterActivity.USER_AGE < 10 || RegisterActivity.USER_AGE > 150 ){
                // If age is out of range make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Age should be between 10 and 150 years", Toast.LENGTH_SHORT).show();
                return;
            }

            // If height is appropriate and valid move to next tab
            RegisterActivity.incrementTab();

        });

        return view;
    }
}
