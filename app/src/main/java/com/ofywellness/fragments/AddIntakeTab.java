package com.ofywellness.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ofywellness.AddMealActivity;
import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;

import java.util.HashMap;

/**
 * Fragment for AddMealTab tab in Home page
 */
public class AddIntakeTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and store it,
        // modification for getting the view object
        View view = inflater.inflate(R.layout.fragment_add_intake_tab, container, false);

        // Button for moving to add meal activity
        view.findViewById(R.id.add_meal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity with intent to move to add meal activity
                startActivity(new Intent(requireActivity(), AddMealActivity.class));
            }
        });

        // Call method to add functionality to update the medicine intake locally (not on database)
        addLocalMedicineAndOtherFieldUpdatingFunctionality(view);

        // Call the method to update the prescription ( 10 in "1 of 10" ) as soon as the page loads
        ofyDatabase.getPrescriptionAndUpdateViews(view.findViewById(R.id.sub_fragment_medicine_linear_layout)
                , requireActivity());

        // On Click Listener to update today's medicine intake "on database"
        view.findViewById(R.id.add_medicine_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get a hash map to store all medicine intakes
                HashMap<String, Integer> tempIntake = new HashMap<>();

                // Call the method to add medicine name and intake to the HashMap
                addMedicineNameAndIntake(tempIntake, view);

                // Call the method to save medicine intake to the database
                ofyDatabase.saveMedicineIntake(tempIntake, requireActivity());

            }
        });

        // On Click Listener to  update the medicine prescription from the database ( 10 in "1 of 10" values )
        view.findViewById(R.id.add_medicine_reload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the method to update the medicine prescription from the database
                ofyDatabase.getPrescriptionAndUpdateViews(view.findViewById(R.id.sub_fragment_medicine_linear_layout)
                        , requireActivity());
            }
        });


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


        // Now functionality for other fields
        // Functionality for water intake (here water intake has same listeners as of medicine buttons )
        view.findViewById(R.id.add_other_add_water_card)
                .setOnClickListener(getListener(view, R.id.add_other_water_detail_label, true));
        view.findViewById(R.id.add_other_reduce_water_card)
                .setOnClickListener(getListener(view, R.id.add_other_water_detail_label, false));

        // Now we add functionality for water intake ( this is quite different from others )
        // Now we set the listener for adding weight 
        view.findViewById(R.id.add_other_add_weight_card)
                .setOnClickListener(getWeightListener(view, true));

        // Now we set the listener for reducing weight
        view.findViewById(R.id.add_other_reduce_weight_card)
                .setOnClickListener(getWeightListener(view, false));
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

    // Method to get on click listeners for buttons to update users weight field
    private View.OnClickListener getWeightListener(View view, boolean increment) {

        // Create new OnClickListener to return via method so as to add clicking functionality to button
        View.OnClickListener newOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // First we get the weight label
                TextView weightLabel = view.findViewById(R.id.add_other_weight_detail_label);

                // Now we get the current weight detail
                String weightDetail = weightLabel.getText().toString();

                // If current weight detail has default text then input sample weight and return
                if (weightDetail.contains("Lose")) {
                    // Set the sample weight
                    weightLabel.setText("50Kg");
                    return;
                }

                // As weight detail has current weight (and not default text), get current weight
                int currentWeight = Integer.parseInt(weightDetail.substring(0, weightDetail.indexOf("Kg")));

                // Now we update weight accordingly
                if (increment)
                    // If the need is to increment the weight, increment it
                    currentWeight++;
                else
                    // Else reduce current weight, but only if it is greater than zero to avoid negative weight
                    currentWeight = currentWeight > 0 ? currentWeight - 1 : 0;

                // Assign new weight detail
                weightDetail = currentWeight + "Kg";

                // Set new weight detail
                weightLabel.setText(weightDetail);

            }
        };

        // Return the created on click listener
        return newOnClickListener;
    }

    // Method to add medicine's name and intake to the provided HashMap
    private void addMedicineNameAndIntake(HashMap<String, Integer> intake, View view) {

        // Add all medicine names and their respective intake to the provided HashMap

        // Add the name and intake of medicine number 1
        intake.put(((TextView) view.findViewById(R.id.add_medicine_name_1))
                .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_1)));

        // Add the name and intake of medicine number 2
        intake.put(((TextView) view.findViewById(R.id.add_medicine_name_2))
                .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_2)));

        // Add the name and intake of medicine number 3
        intake.put(((TextView) view.findViewById(R.id.add_medicine_name_3))
                .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_3)));

        // Add the name and intake of medicine number 4
        intake.put(((TextView) view.findViewById(R.id.add_medicine_name_4))
                .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_4)));

        // Add the name and intake of medicine number 5
        intake.put(((TextView) view.findViewById(R.id.add_medicine_name_5))
                .getText().toString(), getIntake(view.findViewById(R.id.add_medicine_field_5)));

    }


}