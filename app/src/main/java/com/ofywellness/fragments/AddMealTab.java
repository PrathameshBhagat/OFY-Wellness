package com.ofywellness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for AddMealTab tab in Home page
 */
public class AddMealTab extends Fragment {
    Spinner mealTypeSpinner;
    private EditText mealNameTextView, mealEnergyTextView, mealProteinsTextView, mealFatsTextView, mealCarbohydratesTextView, mealType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and store it,
        // modification for the spinner
        View view = inflater.inflate(R.layout.fragment_add_meal_tab, container, false);

        // Get the spinner object and set listeners for events
        mealTypeSpinner = (Spinner) view.findViewById(R.id.meal_type_spinner);

        // Add working to the meal type spinner
        addSpinnerForMealType();

        // Get all objects required from layout
        mealNameTextView = view.findViewById(R.id.meal_name_field);
        mealEnergyTextView = view.findViewById(R.id.meal_energy_field);
        mealProteinsTextView = view.findViewById(R.id.meal_protein_field);
        mealFatsTextView = view.findViewById(R.id.meal_fats_field);
        mealCarbohydratesTextView = view.findViewById(R.id.meal_carbohydrates_field);

        // WARNING Careful!!
        mealTypeSpinner.getSelectedItem().toString();


        return view;
    }

    private void addSpinnerForMealType() {

        // Array List to add options to spinner
        List<String> options = new ArrayList<>();
        options.add("Breakfast");
        options.add("Lunch");
        options.add("Dinner");

        // Create adapter for spinner and add adapter to spinner and set default value
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(arrayAdapter);
        mealTypeSpinner.setSelection(0);
    }

}