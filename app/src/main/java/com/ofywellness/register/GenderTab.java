package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ofywellness.R;

// Tab for getting user's gender
public class GenderTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_gender, container, false);

        // Set gender to null initially
        RegisterActivity.USER_GENDER = null;

        // Set listener for female card
        view.findViewById(R.id.register_gender_female_card).setOnClickListener((v) -> {

            // Set the user's gender as female
            RegisterActivity.USER_GENDER = "Female";

            // Update card elevation for proper indication of user's selection
            ((CardView)view.findViewById(R.id.register_gender_female_card)).setCardElevation(20);
            ((CardView)view.findViewById(R.id.register_gender_male_card)).setCardElevation(0);

        });

        // Set listener for male card
        view.findViewById(R.id.register_gender_male_card).setOnClickListener((v) -> {

            // Set the user's gender as female
            RegisterActivity.USER_GENDER = "Male";

            // Update card elevation for proper indication of user's selection
            ((CardView)view.findViewById(R.id.register_gender_male_card)).setCardElevation(20);
            ((CardView)view.findViewById(R.id.register_gender_female_card)).setCardElevation(0);

        });

        // Set onclick listener to the next button
        view.findViewById(R.id.register_next_gender_button).setOnClickListener((v) -> {

            // If the user's gender is not set then show a toast message to user and return
            if (RegisterActivity.USER_GENDER == null){
                Toast.makeText(requireActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
                return;
            }

            // If gender is appropriate and valid move to next tab
            RegisterActivity.incrementTab();

        });
        return view;

    }

    // Method to be called when this tab resumes
    @Override
    public void onResume() {
        super.onResume();
        // Set label to ask user to select gender
        ((RegisterActivity) requireActivity()).setLabel("Please select your Gender");
    }
}
