package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

public class HeightTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_height, container, false);

        // Set onclick listener to the next button
        view.findViewById(R.id.register_next_height_button).setOnClickListener((v) -> {

            // Get the height provided by user
            String height = ((TextView) view.findViewById(R.id.register_height_field)).getText().toString();

            // Try to parse the height as integer and set users height
            try {
                // Set users height
                RegisterActivity.USER_HEIGHT = Integer.parseInt(height);
            } catch (NumberFormatException e) {
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
}
