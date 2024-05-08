package com.ofywellness.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;
import com.ofywellness.UpdateDietTargetActivity;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Remove the TextView assignment and context to database operation method */

    private TextView energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel, dietDateLabel, mealEnergyLabel, mealProteinsLabel, mealFatsLabel, mealCarbohydratesLabel, mealTypeLabel, mealNameLabel, mealNumberLabel;
    private ProgressBar energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar;
    private ImageView mealImageLabel;
    private int INDEX_OF_MEAL_TO_VIEW;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view for this fragment and inflate it
        View view = inflater.inflate(R.layout.fragment_track_diet_tab, container, false);

        // Assign the text views so that tracking data can be set
        energyValueLabel = view.findViewById(R.id.track_energy_display_label);
        proteinsValueLabel = view.findViewById(R.id.track_protein_display_label);
        fatsValueLabel = view.findViewById(R.id.track_fats_display_label);
        carbohydratesValueLabel = view.findViewById(R.id.track_carbohydrates_display_label);

        // Assign the text views so that the meal can be viewed
        dietDateLabel = view.findViewById(R.id.track_view_meal_date_field);
        mealEnergyLabel = view.findViewById(R.id.track_view_meal_energy_field);
        mealProteinsLabel = view.findViewById(R.id.track_view_meal_protein_field);
        mealFatsLabel = view.findViewById(R.id.track_view_meal_fats_field);
        mealCarbohydratesLabel = view.findViewById(R.id.track_view_meal_carbohydrates_field);
        mealTypeLabel = view.findViewById(R.id.track_view_meal_type_field);
        mealNameLabel = view.findViewById(R.id.track_view_meal_name_field);
        mealNumberLabel = view.findViewById(R.id.track_view_meal_meal_number_field);
        mealImageLabel = view.findViewById(R.id.track_view_meal_meal_image);
        // Set the meal viewing index to zero to view the first meal
        INDEX_OF_MEAL_TO_VIEW = 0;

        // Assign the progress bars so that the progress can be shown
        energyProgressBar = view.findViewById(R.id.track_energy_progress_bar);
        proteinsProgressBar = view.findViewById(R.id.track_protein_progress_bar);
        fatsProgressBar = view.findViewById(R.id.track_fats_progress_bar);
        carbohydratesProgressBar = view.findViewById(R.id.track_carbohydrates_progress_bar);

        // Update tracking tracking data as soon as this tab loads
        updateDietTrackingData();

        // Add on click listener to the update tracking data
        view.findViewById(R.id.track_update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update tracking data each time user clicks update intake button
                updateDietTrackingData();
            }
        });

        // Add on click listener to the update diet target button
        view.findViewById(R.id.track_update_target_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to update diet target activity
                startActivity(new Intent(requireActivity(),UpdateDietTargetActivity.class));

            }
        });

        // Add on click listener to the update the progress
        view.findViewById(R.id.track_update_progress_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update progress each time user clicks update progress button
                updateProgress();

            }
        });

        // Add on click listener to the set date button
        view.findViewById(R.id.track_view_meal_set_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Function to set date and update the meal data
                setDateAndUpdateData();

            }
        });

        view.findViewById(R.id.track_view_meal_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Increment the index to view next meal
                INDEX_OF_MEAL_TO_VIEW++;

                // Display the meal to the user
                displayTheMeal();

            }
        });

        view.findViewById(R.id.track_view_meal_previous_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Decrement the index to view next meal
                INDEX_OF_MEAL_TO_VIEW--;

                // Display the meal to the user
                displayTheMeal();

            }
        });


        // Return view to onCreateView method and the method
        return view;
    }


    // Function to get all meals and display meal at index to user
    private void displayTheMeal() {

        try {

            // If  date not set, show toast message and return
            if (dietDateLabel.getText().equals("DD/MM/YYYY")) {
                Toast.makeText(requireActivity(), "Please set the date first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtain all meals ( of a particular day, from cache not database)
            ArrayList<Meal> obtainedMeals = ofyDatabase.getMeals();

            // If  meals not found, show toast message and return
            if (obtainedMeals.isEmpty()) {
                Toast.makeText(requireActivity(), "Error, Please change/reset the date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make sure that index is in bounds
            if (INDEX_OF_MEAL_TO_VIEW < 0)
                INDEX_OF_MEAL_TO_VIEW = 0;

            // Make sure that index is in bounds
            if (INDEX_OF_MEAL_TO_VIEW >= obtainedMeals.size())
                INDEX_OF_MEAL_TO_VIEW = obtainedMeals.size() - 1 ;

            // Get the meal data to display to the user
            Meal mealToView = obtainedMeals.get(INDEX_OF_MEAL_TO_VIEW);

            // Set the text views to display the meal to the user
            mealNameLabel.setText(mealToView.getName());
            mealNumberLabel.setText(String.format("Meal: %s/%s", INDEX_OF_MEAL_TO_VIEW + 1, obtainedMeals.size()));
            mealEnergyLabel.setText(String.format("%sCal", mealToView.getEnergy()));
            mealProteinsLabel.setText(String.format("%sg", mealToView.getProteins()));
            mealFatsLabel.setText(String.format("%sg", mealToView.getFats()));
            mealCarbohydratesLabel.setText(String.format("%sg", mealToView.getCarbohydrates()));

            // Meal type is in elongated format and also has image source,
            // So we first get the meal type and set it
            String mealType = mealToView.getImage();
            mealType = mealType.substring(0, mealType.indexOf("at"));
            mealTypeLabel.setText(mealType);

            // Now we get the image address and set the url
            String imageAdderess = mealToView.getImage();
            imageAdderess = imageAdderess.substring(imageAdderess.indexOf("https:"));
            URL newurl  = new URL(imageAdderess);
            mealImageLabel.setImageBitmap(null);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                       // URL newurl  = new URL("https://images.pexels.com/photos/674010/pexels-photo-674010.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1");
                        Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mealImageLabel.setImageBitmap(mIcon_val);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(requireActivity(), "Error in getting and setting data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Method to set date and update the meals
    void setDateAndUpdateData() {

        // Create a date picker dialog and set onchange listener
        new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // Set the date in the text view for user
                // Remember LocalDate's month is deviating due to nature of LocalDate....getMonthValue()
                dietDateLabel.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                // Set the meals according to the day
                ofyDatabase.setMealsOfTheDay(requireActivity(), year, month, dayOfMonth);

            }
        }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth()).show();

    }

    // Update tracking data each time user clicks update button
    void updateDietTrackingData() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "updated" tracking data and set the text views to the tracking data
            ofyDatabase.getTrackDietDataAndSetData(requireActivity(), energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    void updateProgress() {

        // Simple try catch block to catch any errors and exceptions
        try {
            // Variables required to get current total diet intake
            int currentEnergy, currentProteins , currentFats, currentCarbohydrates;

            // Get the current progress
            currentEnergy = Integer.parseInt(energyValueLabel.getText().toString().replace("Cal", ""));
            currentProteins = Integer.parseInt(proteinsValueLabel.getText().toString().replace("g", ""));
            currentFats = Integer.parseInt(fatsValueLabel.getText().toString().replace("g", ""));
            currentCarbohydrates = Integer.parseInt(carbohydratesValueLabel.getText().toString().replace("g", ""));

            // Store in a meal object
            Meal currentProgress = new Meal(null,null,
                    currentEnergy ,
                    currentProteins ,
                    currentFats ,
                    currentCarbohydrates );

            // Call the method to update the progress
            ofyDatabase.updateDietProgress( requireActivity(), currentProgress,energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar) ;

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}