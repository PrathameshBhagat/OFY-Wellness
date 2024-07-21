package com.ofywellness.fragments;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class ViewMealTab extends Fragment {
    ArrayList<Meal> obtainedMeals;
    private TextView dietDateLabel, mealEnergyLabel, mealProteinsLabel, mealFatsLabel, mealCarbohydratesLabel, mealTypeLabel, mealNameLabel, mealNumberLabel;
    private TextView summaryEnergyPercentLabel, summaryEnergyRemainsLabel, summaryEnergyConsumedLabel, summaryProteinsLabel, summaryFatsLabel, summaryCarbohydratesLabel;
    private ProgressBar summaryEnergyProgressBar, summaryProteinsProgressBar, summaryFatsProgressBar, summaryCarbohydratesProgressBar;
    private ImageView mealImageLabel;
    private int INDEX_OF_MEAL_TO_VIEW, YEAR, MONTH, DAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment and store it,
        // modification for getting the view object
        View view = inflater.inflate(R.layout.fragment_view_meal, container, false);


        // Assign the views so that the meal can be viewed
        dietDateLabel = view.findViewById(R.id.view_meal_date_field);
        mealEnergyLabel = view.findViewById(R.id.view_meal_energy_field);
        mealProteinsLabel = view.findViewById(R.id.view_meal_protein_field);
        mealFatsLabel = view.findViewById(R.id.view_meal_fats_field);
        mealCarbohydratesLabel = view.findViewById(R.id.view_meal_carbohydrates_field);
        mealTypeLabel = view.findViewById(R.id.view_meal_type_field);
        mealNameLabel = view.findViewById(R.id.view_meal_name_field);
        mealNumberLabel = view.findViewById(R.id.view_meal_meal_number_field);
        mealImageLabel = view.findViewById(R.id.view_meal_meal_image_field);

        // Views for summary card
        summaryEnergyPercentLabel = view.findViewById(R.id.view_summary_energy_percent_label);
        summaryEnergyRemainsLabel = view.findViewById(R.id.view_summary_energy_remainging_label);
        summaryEnergyConsumedLabel = view.findViewById(R.id.view_summary_energy_consumed_label);
        summaryProteinsLabel = view.findViewById(R.id.view_summary_protein_value);
        summaryFatsLabel = view.findViewById(R.id.view_summary_fats_value);
        summaryCarbohydratesLabel = view.findViewById(R.id.view_summary_carbohydrates_value);

        // Progress bars for summary card
        summaryEnergyProgressBar = view.findViewById(R.id.view_summary_energy_percent_circular_progressbar);
        summaryProteinsProgressBar = view.findViewById(R.id.view_summary_protein_progressbar);
        summaryFatsProgressBar = view.findViewById(R.id.view_summary_fats_progressbar);
        summaryCarbohydratesProgressBar = view.findViewById(R.id.view_summary_carbohydrates_progressbar);


        // Set the meal viewing index to zero to view the first meal
        INDEX_OF_MEAL_TO_VIEW = 1;

        // Set today's date
        YEAR = LocalDate.now().getYear();
        MONTH = LocalDate.now().getMonthValue();
        DAY = LocalDate.now().getDayOfMonth();

        // Update the data for the first time
        // Subsequent updates will be triggered by onclick and other listeners
        updateMealAndMedicineData(view);


        // Add on click listener to the set date button
        dietDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Function to set date and update the meal data
                setDateAndUpdateData(view);

            }
        });

        // Add on click listener to the view next meal
        view.findViewById(R.id.view_meal_next_button_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Increment the index to view next meal
                INDEX_OF_MEAL_TO_VIEW++;

                // Display the meal to the user
                displayTheMeal();

            }
        });

        // Add on click listener to the view previous meal
        view.findViewById(R.id.view_meal_previous_button_image ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Decrement the index to view next meal
                INDEX_OF_MEAL_TO_VIEW--;

                // Display the meal to the user
                displayTheMeal();

            }
        });


        // return the view for this fragment
        return view;
    }


    // Function to get all meals and display meal at index to user, is public for ofyDatabase.setMealsOfTheDay method
    public void displayTheMeal() {

        try {

            // If  date not set, show toast message and return
            if (dietDateLabel.getText().equals("DD/MM/YYYY")) {
                Toast.makeText(requireActivity(), "Please set the date first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtain all meals of a particular day
            // ( Meals already obtained from the database and saved in allMeals Found variable )
            // ( We just get the meals from that variable )
            obtainedMeals = ofyDatabase.getMeals();

            // If  meals not found, show toast message and return
            if (obtainedMeals.isEmpty()) {

                // Show the toast message
                Toast.makeText(requireActivity(), "No meals found for this date", Toast.LENGTH_SHORT).show();

                // Set the text views to display that no meal was found
                mealNameLabel.setText("Not found");
                mealTypeLabel.setText("Sorry");
                mealNumberLabel.setText(String.format("%s/%s", 0,0));
                mealEnergyLabel.setText(String.format("%sCal", 0));
                mealProteinsLabel.setText(String.format("%sg", 0));
                mealFatsLabel.setText(String.format("%sg", 0));
                mealCarbohydratesLabel.setText(String.format("%sg", 0));
                mealImageLabel.setImageResource(R.drawable.food_icon);

                // Now set the summary card to empty values
                setSummary();

                // Now return as no need to display the contents
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

            // Now set the summary card
            setSummary();

            // Set the text views to display the meal to the user
            mealNameLabel.setText(mealToView.getName());
            mealNumberLabel.setText(String.format("%s/%s", INDEX_OF_MEAL_TO_VIEW + 1, obtainedMeals.size()));
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

    // Method to set the summary card
    private void setSummary() {

        // Obtain all meals the day
        // ( Meals already obtained from the database and saved in allMealsFound variable )
        // ( We just get the meals from that variable )
        obtainedMeals = ofyDatabase.getMeals();

        // Variables to store calculated values
        int[] totalNutrients = new int[4];

        // Loop through all meals and calculate total nutrient intake
        for (Meal meal : obtainedMeals) {

            // Calculate total nutrients
            totalNutrients[0] += meal.getEnergy();
            totalNutrients[1] += meal.getProteins();
            totalNutrients[2] += meal.getFats();
            totalNutrients[3] += meal.getCarbohydrates();

        }

        // Now set the labels with appropriate data
        summaryEnergyPercentLabel.setText(String.format("%d", (int) (((float) totalNutrients[0] / 2500) * 100)) + "%");
        summaryEnergyRemainsLabel.setText((totalNutrients[0] > 2500 ? 0 : 2500 - totalNutrients[0]) + "Kcal\n Left");
        summaryEnergyConsumedLabel.setText(totalNutrients[0] + "Kcal\n Taken");
        summaryProteinsLabel.setText(totalNutrients[1] + "g/55g");
        summaryFatsLabel.setText(totalNutrients[2] + "g/25g");
        summaryCarbohydratesLabel.setText(totalNutrients[3] + "g/275g");

        // Now set the progress bars with appropriate data
        summaryEnergyProgressBar.setProgress(100 * totalNutrients[0] / 2500);
        summaryProteinsProgressBar.setProgress(100 * totalNutrients[1] / 55);
        summaryFatsProgressBar.setProgress(100 * totalNutrients[2] / 25);
        summaryCarbohydratesProgressBar.setProgress(100 * totalNutrients[3] / 275);


    }


    // Method to set date and update the medicine and meal data according to the date
    void setDateAndUpdateData(View view) {

        // Create a date picker dialog and set onchange listener
        new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                // Set the date variables with the selected date values
                // Set year
                YEAR = year;
                // Set month but be careful as date picker lags by one month
                MONTH = month + 1 ;
                // Set the day
                DAY = dayOfMonth;

                // Call the method to set the meal data and update medicine intake
                updateMealAndMedicineData(view);

            }
        }, LocalDate.now().getYear(), LocalDate.now().getMonthValue() - 1, LocalDate.now().getDayOfMonth()).show();

    }

    // Method to update meals and medicine intake and show it to the user
    private void updateMealAndMedicineData(View view) {

        // Set the date in the text view for user to view it
        dietDateLabel.setText(DAY + " " + Month.of(MONTH) + " " + YEAR);

        // Call the method to show the medicine intake of updated date
        showUpdatedMedicineIntake(view);

        // Now we set the meals from the database according to the day
        //
        // This procedure is very different from others
        // Below method gets the meal data from database and updates it in the variable called allMealsFound
        // We then read this allMealsFound variable and show meals to the user
        // by calling the displayTheMeal method and getting allMealsFound data by the getMeals method
        ofyDatabase.setMealsOfTheDay(requireActivity(), YEAR, MONTH - 1, DAY, this);

    }

    // Method to show updated medicine intake
    private void showUpdatedMedicineIntake(View view) {

        // Simple try catch block
        try {

            // Call database method to show medicine intake of the date,
            // For this we provide the layout to add medicine cards to
            ofyDatabase.getMedicineAndUpdateViews(
                    // Provide the method with the view to add medicine intake display cards to
                    view.findViewById(R.id.view_medicine_container_linear_layout),
                    // Provide context
                    requireActivity(),
                    // Provide the date of which medicine intake is to be viewed
                    LocalDate.of(YEAR, MONTH, DAY).toString());

        }catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(requireActivity(), "Error in getting medicine intake ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }

    }
}