package com.ofywellness.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

public class ViewMealTab extends Fragment {
    private TextView dietDateLabel, mealEnergyLabel, mealProteinsLabel, mealFatsLabel, mealCarbohydratesLabel, mealTypeLabel, mealNameLabel, mealNumberLabel;

    private ImageView mealImageLabel;
    private int INDEX_OF_MEAL_TO_VIEW;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_meal, container, false);


        // Assign the views so that the meal can be viewed
        dietDateLabel = view.findViewById(R.id.track_view_meal_date_field);
        mealEnergyLabel = view.findViewById(R.id.track_view_meal_energy_field);
        mealProteinsLabel = view.findViewById(R.id.track_view_meal_protein_field);
        mealFatsLabel = view.findViewById(R.id.track_view_meal_fats_field);
        mealCarbohydratesLabel = view.findViewById(R.id.track_view_meal_carbohydrates_field);
        mealTypeLabel = view.findViewById(R.id.track_view_meal_type_field);
        mealNameLabel = view.findViewById(R.id.track_view_meal_name_field);
        mealNumberLabel = view.findViewById(R.id.track_view_meal_meal_number_field);
        mealImageLabel = view.findViewById(R.id.track_view_meal_meal_image_field);


        // Set the meal viewing index to zero to view the first meal
        INDEX_OF_MEAL_TO_VIEW = 0;

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


        // Inflate the layout for this fragment
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

            // Set the image view to default logo image
            mealImageLabel.setImageResource(R.drawable.logo_white_nobg_cropped);

            // Now we will download the image from provided url in a background thread
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        // Get the image from internet/database
                        Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                        // Now we need to update the image view,
                        // But for this we need to be on the UI thread so
                        // Set the image on the UI thread
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Set the image view
                                mealImageLabel.setImageBitmap(mIcon_val);
                            }
                        });
                    } catch (Exception e) {
                        // If file not found show toast message
                        // But we need to be in UI thread for this
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireActivity(),"Image not found!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
            // Above start call starts the thread
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


}