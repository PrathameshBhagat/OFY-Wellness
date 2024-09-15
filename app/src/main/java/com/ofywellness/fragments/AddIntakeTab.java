package com.ofywellness.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.ofywellness.AddMealActivity;
import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;

import java.util.HashMap;

/**
 * Fragment for AddMealTab tab in Home page
 */
public class AddIntakeTab extends Fragment {

    // Context for tap target sequence, needs to removed on optimization
    static Context context;

    // View for tap target sequence, needs to removed on optimization
    static View view;

    /**
     * Lets users know what a button does and what are its features
     */
    public static void educateUserAboutButtons() {

        // Create a sequence to let users know what buttons do
        new TapTargetSequence((Activity) context).targets(

                // Now add tap targets to the sequence to show
                TapTarget.forView(view.findViewById(R.id.add_meal_button), "This is add meal intake button ", "You can add all your meals by clicking here "),
                TapTarget.forView(view.findViewById(R.id.add_medicine_add_button_1), "This is increment medicine intake button ", "You can add all your medicine intake and increment particular intake by clicking here "),
                TapTarget.forView(view.findViewById(R.id.add_medicine_reduce_button_1), "This is decrement medicine intake button ", "You can decrement your medicine intake by clicking here ")

        )
        // Now start the sequence
        .start();

        // Set the objects to null as they can lead to memory leaks
        view = null;
        context = null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and store it,
        // modification for getting the view object
        View view = inflater.inflate(R.layout.fragment_add_intake_tab, container, false);

        // Assign context and view for use by Home Activity
        context = requireActivity();

        AddIntakeTab.view = view;

        // Button for moving to add meal activity
        view.findViewById(R.id.add_meal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity with intent to move to add meal activity
                startActivity(new Intent(requireActivity(), AddMealActivity.class).putExtra("ID",getActivity().getIntent().getStringExtra("ID")));
            }
        });

        // Call method to add functionality to update the medicine intake locally (not on database)
        addLocalMedicineAndOtherFieldUpdatingFunctionality(view);

        // Call the method to update the prescription ( 10 in "1 of 10" ) as soon as the page loads
        ofyDatabase.getPrescriptionAndUpdateViews(view.findViewById(R.id.sub_fragment_medicine_linear_layout)
                , requireActivity());

        // Call the method to get today's logged all other measures and display them to user by updating views
        ofyDatabase.getTodaysLoggedOtherMeasuresAndUpdateViews(view, this);

        return view;
    }

    // Method to add functionality to update fields locally (these updates are not reflected in database)
    void addLocalMedicineAndOtherFieldUpdatingFunctionality(View view) {

        // Now we add local functionality to update the medicine intake and other fields

        // First functionality for medicines
        // Functionality for medicine 1
        view.findViewById(R.id.add_medicine_add_button_1)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_1, true));
        view.findViewById(R.id.add_medicine_reduce_button_1)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_1, false));

        // Functionality for medicine 2
        view.findViewById(R.id.add_medicine_add_button_2)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_2, true));
        view.findViewById(R.id.add_medicine_reduce_button_2)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_2, false));

        // Functionality for medicine 3
        view.findViewById(R.id.add_medicine_add_button_3)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_3, true));
        view.findViewById(R.id.add_medicine_reduce_button_3)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_3, false));

        // Functionality for medicine 4
        view.findViewById(R.id.add_medicine_add_button_4)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_4, true));
        view.findViewById(R.id.add_medicine_reduce_button_4)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_4, false));

        // Functionality for medicine 5
        view.findViewById(R.id.add_medicine_add_button_5)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_5, true));
        view.findViewById(R.id.add_medicine_reduce_button_5)
                .setOnClickListener(getListener(view, R.id.add_medicine_field_5, false));


        // Now functionality for other fields ( these are quite different from others )
        // Functionality for water intake (here water intake has same listeners as of medicine buttons )
        view.findViewById(R.id.add_other_add_water_card)
                .setOnClickListener(getOtherListener(view, R.id.add_other_water_detail_label, true));
        view.findViewById(R.id.add_other_reduce_water_card)
                .setOnClickListener(getOtherListener(view, R.id.add_other_water_detail_label, false));

        // Now we add functionality for weight
        // Now we set the listener for adding weight 
        view.findViewById(R.id.add_other_add_weight_card)
                .setOnClickListener(getOtherListener(view, R.id.add_other_weight_detail_label, true));

        // Now we set the listener for reducing weight
        view.findViewById(R.id.add_other_reduce_weight_card)
                .setOnClickListener(getOtherListener(view, R.id.add_other_weight_detail_label, false));
    }

    // Method to get on click listeners for buttons to update respective fields
    View.OnClickListener getListener(View view, int rID, boolean increment) {

        // First get the field to read and update it
        TextView field = view.findViewById(rID);

        // Create new OnClickListener to return via the method so as to add clicking functionality to button
        View.OnClickListener newOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the getIntake method and get current intake
                int intakeCount = getIntake(field);

                // Update the intake accordingly
                if (increment) {

                    // Increment only if increment is true and intake than medicine prescribed (do not merge with &&)
                    if (intakeCount < getRecommendedIntake(field))

                        // And call setIntake to set the incremented intake
                        setIntake(field, intakeCount + 1);

                } else if (intakeCount > 0)

                    // Else if increment variable is false (i.e. we need to decrement)
                    // And intake is greater than zero then decrement intake
                    // And call setIntake to set the decremented intake
                    setIntake(field, intakeCount - 1);


                // Get a hash map to store all medicine intakes
                HashMap<String, Integer> tempIntake = new HashMap<>();

                // Call the method to add medicine name and intake to the HashMap
                addMedicineNameAndIntake(tempIntake, view);

                // Call the method to save medicine intake to the database
                ofyDatabase.saveMedicineIntake(tempIntake, requireActivity());

            }
        };

        //return the created OnClickListener
        return newOnClickListener;

    }

    // Method to get the intake form the provided fields
    private int getIntake(TextView field) {

        // Get the current intake (in string variable) from the field
        String intakeString = field.getText().toString();

        // Return the current intake of the field (in "int" format)
        return Integer.parseInt(intakeString.substring(0, intakeString.indexOf(" of ")));

    }

    // Method to set the field with the provided intake
    private void setIntake(TextView field, int intake) {

        // Get the current intake (in string variable) from the field with excess characters
        String intakeString = field.getText().toString();

        // Trim extra characters and store remaining characters in same variable
        intakeString = intakeString.substring(intakeString.lastIndexOf(" of "));

        // Update the intake string with provided intake value
        intakeString = intake + intakeString;

        // Set the intake
        field.setText(intakeString);

    }

    // Method to get the recommended intake value from the field
    private int getRecommendedIntake(TextView field) {

        // Get the intake string from the field
        String intakeString = field.getText().toString();

        // Return the recommended intake of the field (in "int" format)
        return Integer.parseInt(intakeString.substring(intakeString.lastIndexOf(" ") + 1));

    }

    // Method to get on click listeners for buttons to update users other measures
    private View.OnClickListener getOtherListener(View view, int rID, boolean increment) {

        // Create new OnClickListener and return via method to add clicking functionality to other button
        // But as onclick listener is a functional interface we converted it to a lambda function
        return v -> {

            // First we get the measure's data field
            TextView measureField = view.findViewById(rID);

            // Now we get the measure's detail
            String measureDetail = measureField.getText().toString();

            // Variable to save measure's count
            int currentCount = 0;

            // Now we extract the measure's values
            if (measureDetail.contains("- Kg")) {
                // If current measure's detail has default text then we input sample values
                measureField.setText("50 Kg");
                currentCount = 50;

            } else if (measureDetail.contains("Kg")) {
                // Else if the measure's detail has weight value extract it and parse as integer
                currentCount = Integer.parseInt(measureDetail.substring(0, measureDetail.indexOf(" Kg")));

            } else if (measureDetail.contains("Glass")){
                // Else if the measure's detail has water value extract it and parse as integer
                currentCount = Integer.parseInt(measureDetail.substring(0, measureDetail.indexOf(" Glass")));

            }


            // Now we have extracted the value of current measure in the currentCount variable
            // Now we update its value count accordingly
            if (increment)
                // If the need is to increment the count, increment it
                currentCount++;
            else
                // Else reduce current count, but only if it's greater than zero to avoid negative's
                currentCount = currentCount > 0 ? currentCount - 1 : 0;


            // Now we assign new updated values
            if (measureDetail.contains("Kg"))
                // Append Kg if the it's the weight and set the measure
                measureDetail = currentCount + " Kg";
            else
                // Append glass if the it's the water intake and set the measure
                measureDetail = currentCount + " Glass";

            // Set new measure details in text view
            measureField.setText(measureDetail);


            // Now we prepare to send all measure counts to database
            // Get a map to store values
            HashMap<String, Integer> allOtherValues = new HashMap<>();

            // Add all the measures to the map (weight and water)
            // Add weight measure
            allOtherValues.put("Weight", Integer.valueOf(
                    ((TextView) view.findViewById(R.id.add_other_weight_detail_label))
                            .getText().toString().replace(" Kg", "")
                            .replace("-","0")));

            // Add water intake
            allOtherValues.put("Water", Integer.valueOf(
                    ((TextView) view.findViewById(R.id.add_other_water_detail_label))
                            .getText().toString().replace(" Glass", "")));

            // Call the method to save the measure values to the database
            ofyDatabase.saveOtherCounts(allOtherValues, requireActivity());

        };
    }

    // Method to add medicine's name and intake to the provided HashMap
    private void addMedicineNameAndIntake(HashMap<String, Integer> intake, View view) {

        // Add all medicine names and their respective intake to the provided HashMap

        // If intake of medicine number 1 is not 0 then
        if (getIntake(view.findViewById(R.id.add_medicine_field_1)) != 0)
            // Add the name and intake of medicine number 1
            intake.put(((TextView) view.findViewById(R.id.add_medicine_name_1))
                    .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_1)));

        // If intake of medicine number 1 is not 0 then
        if (getIntake(view.findViewById(R.id.add_medicine_field_2)) != 0)
            // Add the name and intake of medicine number 2
            intake.put(((TextView) view.findViewById(R.id.add_medicine_name_2))
                    .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_2)));

        // If intake of medicine number 3 is not 0 then
        if (getIntake(view.findViewById(R.id.add_medicine_field_3)) != 0)
            // Add the name and intake of medicine number 3
            intake.put(((TextView) view.findViewById(R.id.add_medicine_name_3))
                    .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_3)));

        // If intake of medicine number 4 is not 0 then
        if (getIntake(view.findViewById(R.id.add_medicine_field_4)) != 0)
            // Add the name and intake of medicine number 4
            intake.put(((TextView) view.findViewById(R.id.add_medicine_name_4))
                    .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_4)));

        // If intake of medicine number 5 is not 0 then
        if (getIntake(view.findViewById(R.id.add_medicine_field_5)) != 0)
            // Add the name and intake of medicine number 5
            intake.put(((TextView) view.findViewById(R.id.add_medicine_name_5))
                    .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_5)));

    }


}