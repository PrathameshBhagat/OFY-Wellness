package com.ofywellness.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

// Tab for getting user's phone number
public class PhoneTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Modification for getting the view object
        View view = inflater.inflate(R.layout.register_fragment_phone, container, false);

        // Set onclick listener to the next button
        view.findViewById(R.id.register_next_phone_button).setOnClickListener((v) -> {

            // Get the phone number provided by user
            String phone = ((TextView) view.findViewById(R.id.register_phone_field)).getText().toString();

            // Validate phone provided and show appropriate message
            if (phone.isEmpty()) {
                // If phone is empty make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Please enter your phone no.", Toast.LENGTH_SHORT).show();
                return;
            } else if ( phone.length() < 10 ) {
                // If phone is not a 10 digit number make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Phone should be a 10 digit number", Toast.LENGTH_SHORT).show();
                return;
            } else if (phone.contains(".")) {
                // If phone contains a dot (.), make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Phone should not have a dot (.) ", Toast.LENGTH_SHORT).show();
                return;
            } else if (!phone.chars().allMatch(Character::isDigit)) {
                // If phone's all characters are not digits, make a toast to show message to user and return
                Toast.makeText(requireActivity(), "Phone should only contain digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the user's phone number
            RegisterActivity.USER_PHONE = phone;

            // If phone is appropriate and valid move to next tab
             RegisterActivity.registerUser();

        });

        return view;
    }

    // Method to be called when this tab resumes
    @Override
    public void onResume() {
        super.onResume();
        // Set label to ask user to enter phone number
        ((RegisterActivity) requireActivity()).setLabel("Please enter your contact (phone) No.");
    }
}
