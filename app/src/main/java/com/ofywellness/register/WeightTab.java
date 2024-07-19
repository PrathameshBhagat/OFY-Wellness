package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

public class WeightTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_weight, container, false);

        // Set onclick listener to the next button
        view.findViewById(R.id.register_next_weight_button).setOnClickListener((v) -> {

            // Get the weight provided by user
            String weight = ((TextView) view.findViewById(R.id.register_weight_field)).getText().toString();

            // Try to parse the weight as integer and set users height
            try {
                // Set users height
                RegisterActivity.USER_WEIGHT = Integer.parseInt(weight);
            } catch (NumberFormatException e) {
                // If found exception make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Please enter your weight", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if weight is in range
            if (RegisterActivity.USER_WEIGHT < 20 || RegisterActivity.USER_WEIGHT > 200) {
                // If weight is out of range make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Weight should be between 20 and 200 Kg", Toast.LENGTH_SHORT).show();
                return;
            }

            // If weight is appropriate and valid move to next tab
            RegisterActivity.incrementTab();

        });

        return view;
    }
}
