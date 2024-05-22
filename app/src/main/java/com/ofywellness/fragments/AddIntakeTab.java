package com.ofywellness.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ofywellness.AddMealActivity;
import com.ofywellness.R;

/**
 * Fragment for AddMealTab tab in Home page
 */
public class AddIntakeTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and store it,
        // modification for getting the view object
        View view = inflater.inflate(R.layout.fragment_add_intake_tab, container, false);

        view.findViewById(R.id.add_meal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), AddMealActivity.class));
            }
        });

        return view;
    }

}