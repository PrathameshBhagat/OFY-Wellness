package com.ofywellness.db;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.ofywellness.HomeActivity;
import com.ofywellness.R;
import com.ofywellness.fragments.AddIntakeTab;
import com.ofywellness.fragments.TrackDietTab;
import com.ofywellness.fragments.ViewMealTab;
import com.ofywellness.modals.Meal;
import com.ofywellness.modals.User;
import com.ofywellness.register.RegisterActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

// Single class for all database operations
public class ofyDatabase {
    private static DatabaseReference ofyDatabaseref;
    private static ArrayList<Meal> allMealsFound;

    /**
     * Add new user to Firebase Database
     *
     * @param ofyUser The user data model object
     * @param context The context to show toast message
     * @return The auto-generated UserID of this user
     */
    public static String addNewUserToFirebase(User ofyUser, Context context) {
        try {
            // Get database reference
            ofyDatabaseref = FirebaseDatabase.getInstance().getReference();
            // Set operation to push to automatically get unique UserID with a storage location
            ofyDatabaseref = ofyDatabaseref.child("Users").push();
            // Add the user to database
            ofyDatabaseref.setValue(ofyUser);

            // Get the user's key (currently UserID) to identify individual user
            String key = ofyDatabaseref.getKey();

            // Now we move the database reference to a new location
            // So that all the other data gets stored in a separate location to avoid data congestion
            // So we first get to the root and then move to desired location
            ofyDatabaseref = ofyDatabaseref.getRoot().child("Intake").child(key);

            // Return the UserID
            return key;

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error creating new user in database", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get UserID from  Firebase Database if user exists
     *
     * @param ofyUserEmail The email of current user to find in database
     * @param context      The context to show toast message
     */
    public static void findUserInFirebaseAndNext(Context context, String ofyUserEmail) {

        // Get the Firebase Database reference to users
        ofyDatabaseref = FirebaseDatabase.getInstance().getReference();

        // Get the users data and add on complete listener to run method on obtaining data
        ofyDatabaseref.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {
                // Simple try catch block
                try {
                    // boolean to check if we moved to next activity
                    boolean moved = false;

                    // if task is successful move forward
                    if (task.isSuccessful()) {

                        // Loop through all individual users
                        for (DataSnapshot individualUser : task.getResult().getChildren()) {

                            // Get individual users details and map to a hashmap
                            HashMap userDetail = (HashMap) individualUser.getValue();

                            // If user detail has email field with value equal to the obtained email
                            if (userDetail.containsKey("email") && userDetail.get("email").toString().equals(ofyUserEmail)) {

                                // Then finish current activity
                                ((Activity) context).finish();

                                // Make the database reference point to this user
                                ofyDatabaseref = ofyDatabaseref.getRoot().child("Intake").child(individualUser.getKey());
                                // And move to next activity and provide UserID (currently has no usage)
                                startActivity(context, new Intent(context, HomeActivity.class).putExtra("ID", individualUser.getKey()), null);

                                // Also  set this boolean to indicate we moved to next activity
                                moved = true;
                                // Break the loop
                                break;

                            }

                        }

                    }

                    // If not moved to next activity as moved is false
                    // user details was not found and ask user to register his details
                    // by moving to register activity with email address
                    if (!moved) {
                        // Finish current activity and move to register activity with email as extra
                        ((Activity) context).finish();
                        startActivity(context, new Intent(context, RegisterActivity.class)
                                .putExtra("email", ofyUserEmail), null);
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error getting the user in database", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Add meal to user's diet in Firebase Database
     *
     * @param ofyMeal  The meal details
     * @param mealType The type of meal breakfast, lunch, dinner
     * @param context  The context to show toast message
     */
    public static void addMeal(Meal ofyMeal, String mealType, Context context) {
        try {
            // Add meal to proper location,
            // Database ref is already pointing current user
            ofyDatabaseref.child("Diet").child(String.valueOf(LocalDate.now())).child(mealType + " at : " + new Date()).setValue(ofyMeal);
        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error adding the meal", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * Track user's  diet and set TextViews to the tracking data
     *
     * @param trackDietTab          The trackDiet fragment to show toast message
     * @param energyValueLabel All others are TextViews and Progress Bars to set tracking data
     */
    public static void getTrackDietDataAndSetData(TrackDietTab trackDietTab, TextView energyValueLabel, TextView proteinsValueLabel, TextView fatsValueLabel, TextView carbohydratesValueLabel) {

        // Context for showing toast messages
        Context context = trackDietTab.getContext();
        // Get the DataSnapshot to get tracking data, calculate it
        // And display it to user by updating the text views
        ofyDatabaseref.child("Diet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // Check if the data exists
                    if (snapshot.exists()) {
                        // Tracking data variables
                        int totalEnergy = 0, totalProteins = 0, totalFats = 0, totalCarbohydrates = 0;

                        // Loop through all the days
                        for (DataSnapshot ofyDateDataSnapshot : snapshot.getChildren()) {
                            // Loop through all the meals
                            for (DataSnapshot ofyMealSnapshot : ofyDateDataSnapshot.getChildren()) {
                                // Get the content in the HashMap
                                HashMap mealContent = (HashMap) ofyMealSnapshot.getValue();

                                // Calculate the data
                                totalEnergy += Integer.parseInt(mealContent.get("energy").toString());
                                totalProteins += Integer.parseInt(mealContent.get("proteins").toString());
                                totalFats += Integer.parseInt(mealContent.get("fats").toString());
                                totalCarbohydrates += Integer.parseInt(mealContent.get("carbohydrates").toString());

                            }

                        }

                        // Set the text views to show the data
                        energyValueLabel.setText(String.format("%sCal", totalEnergy));
                        proteinsValueLabel.setText(String.format("%sg", totalProteins));
                        fatsValueLabel.setText(String.format("%sg", totalFats));
                        carbohydratesValueLabel.setText(String.format("%sg", totalCarbohydrates));

                        // Now show the updated progress to the user
                        trackDietTab.updateProgress();
                    } else {
                        // Show a toast error message
                        Toast.makeText(context, "Error in getting Diet target", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error in getting Diet target", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch exception, show a toast error message and print error stack
                Toast.makeText(context, "Error in getting Diet target", Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });
    }

    /**
     * Add or change the diet target
     *
     * @param context The context to show toast message
     * @param ofyMeal The meal object with the diet target data
     */
    public static void addDietTarget(Meal ofyMeal, Context context) {
        // simple try catch block
        try {
            // Add meal to proper location,
            // Database ref is already pointing current user
            ofyDatabaseref.child("Target").setValue(ofyMeal);
        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error updating the target ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * Method to update current progress according to the diet target
     *
     * @param context          The context to show toast message
     * @param currentProgress  The meal object with the current diet data
     */
    public static void updateDietProgress(TrackDietTab context, Meal currentProgress, ProgressBar energyProgressBar, ProgressBar proteinsProgressBar, ProgressBar fatsProgressBar, ProgressBar carbohydratesProgressBar) {

        // Database ref is already pointing current user just get the target and update progress
        ofyDatabaseref.child("Target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Simple try catch block
                try {
                    // Check if the task to get data was successful
                    if (snapshot.exists()) {
                        // Diet target  variables
                        int targetEnergy, targetProteins , targetFats, targetCarbohydrates;

                        // Current diet values
                        int currentEnergy, currentProteins , currentFats, currentCarbohydrates;

                        // Get current progress
                        currentEnergy = currentProgress.getEnergy();
                        currentProteins = currentProgress.getProteins();
                        currentFats = currentProgress.getFats();
                        currentCarbohydrates = currentProgress.getCarbohydrates();

                        // Get the diet target from DataSnapshot from the task and convert to HashMap
                        HashMap target = (HashMap) snapshot.getValue();

                        // If the diet target is empty then show the warning and return
                        if ( target.isEmpty() ) {
                            context.warning(true);
                            return;
                        }
                        // Else do not show the warning
                        else
                            context.warning(false);

                        // Get the target values from the map
                        targetEnergy = Integer.parseInt(target.get("energy").toString());
                        targetProteins =  Integer.parseInt(target.get("proteins").toString());
                        targetFats = Integer.parseInt(target.get("fats").toString());
                        targetCarbohydrates = Integer.parseInt(target.get("carbohydrates").toString());

                        // Set the progress bars to proper percentage values they represent
                        energyProgressBar.setProgress( currentEnergy * 100 / targetEnergy );
                        proteinsProgressBar.setProgress( currentProteins * 100 / targetProteins );
                        fatsProgressBar.setProgress( currentFats * 100 / targetFats );
                        carbohydratesProgressBar.setProgress( currentCarbohydrates * 100 / targetCarbohydrates );

                    } else {
                        // If data does not exists then show warning
                        context.warning(true);

                        // Also throw an error with corresponding  message
                        throw new RuntimeException("Diet target not found");
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context.requireActivity(), e.getMessage() , Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch exception, show a toast error message and print error stack
                Toast.makeText(context.requireActivity(), "Error," + error.getMessage() , Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });


    }

    /**
     * Get all the meals of the date provided and store them in an ArrayList object
     *
     * @param context The context to show toast message
     * @param year The year of the date
     * @param month The month of the date
     * @param dayOfMonth The day of the date
     */
    public static void setMealsOfTheDay(Context context, int year, int month, int dayOfMonth, ViewMealTab viewMealTab) {

        // Initialise the ArrayList object
        allMealsFound = new ArrayList<>();

        // Get all the meals eaten on the date provided and them to the ArrayList
        ofyDatabaseref.child("Diet").child(LocalDate.of(year, month + 1, dayOfMonth).toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Simple try catch block
                try {

                    // Clear all the existing meals
                    allMealsFound.clear();

                    // Check if the task to get data was successful
                    if (snapshot.exists()) {
                        // Loop through data of all the days
                        for (DataSnapshot ofyDateDataSnapshot : snapshot.getChildren()) {

                            // Get the meal from DataSnapshot and convert to HashMap
                            HashMap received = (HashMap) ofyDateDataSnapshot.getValue();

                            // Get the meal content from the map into a new Meal object,
                            // But, Image will contain meal type, time and image url for ease of transfer
                            Meal receivedMeal = new Meal(
                                    ofyDateDataSnapshot.getKey()+"::"+received.get("image").toString(),
                                    received.get("name").toString(),
                                    Integer.parseInt(received.get("energy").toString()),
                                    Integer.parseInt(received.get("proteins").toString()),
                                    Integer.parseInt(received.get("fats").toString()),
                                    Integer.parseInt(received.get("carbohydrates").toString()));

                            // Add all the meals received to later retrieve them 
                            allMealsFound.add(receivedMeal);

                        }

                    }

                    // Now display all the meals
                    viewMealTab.displayTheMeal();

                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch exception, show a toast error message and print error stack
                Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });

    }

    /**
     * Get all the meals eaten in a day which was stored earlier
     */
    public static ArrayList<Meal> getMeals() {

        return allMealsFound;

    }


    /**
     * Gets all the diet intake data and sets the charts
     *
     * @param trackDietTab Track Diet tab object to call it's methods
     */
    public static void getDietDataAndSetNutrientCharts(TrackDietTab trackDietTab) {

        // Get context for showing toast messages
        Context context = trackDietTab.getContext();

        // Query the last 7 records of the required data, ordered by their keys
        Query query = ofyDatabaseref.child("Diet").orderByKey().limitToLast(7);

        // Add a listener to listen to all changes to the data and show updated data to the user
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the data exists
                if (snapshot.exists()) {

                    // Create linked hash maps to store the total intake of each day in an orderly manner
                    // Map to store energy
                    LinkedHashMap<String, Float> energyMap = new LinkedHashMap<>();
                    // Map to store proteins intake
                    LinkedHashMap<String, Float> proteinsMap = new LinkedHashMap<>();
                    // Map to store fats intake
                    LinkedHashMap<String, Float> fatsMap = new LinkedHashMap<>();
                    // Map to store carbohydrate intake
                    LinkedHashMap<String, Float> carbohydratesMap = new LinkedHashMap<>();

                    // Variables to calculate and store total nutrient of a particular day
                    float totalEnergy, totalProteins, totalFats, totalCarbohydrates;

                    // Loop through all the days
                    for (DataSnapshot ofyDateDataSnapshot : snapshot.getChildren()) {

                        // Reset the variables for "this" day
                        totalEnergy = 0;
                        totalProteins = 0;
                        totalFats = 0;
                        totalCarbohydrates = 0;

                        // Loop through all the meals taken on this day
                        for (DataSnapshot ofyMealSnapshot : ofyDateDataSnapshot.getChildren()) {

                            // Convert meal data into a HashMap
                            HashMap mealContent = (HashMap) ofyMealSnapshot.getValue();

                            // Calculate the total intake of "this" day
                            totalEnergy += (Long) mealContent.get("energy");
                            totalProteins += (Long) mealContent.get("proteins");
                            totalFats += (Long) mealContent.get("fats");
                            totalCarbohydrates += (Long) mealContent.get("carbohydrates");

                        }
                        // Get the current date, which is the key of this location
                        String date = ofyDateDataSnapshot.getKey();

                        // Add this days intake to the corresponding maps
                        energyMap.put(date, totalEnergy);
                        proteinsMap.put(date, totalProteins);
                        fatsMap.put(date, totalFats);
                        carbohydratesMap.put(date, totalCarbohydrates);

                    }

                    // Get a linked hash map to store current nutrient map to display,
                    // As only one nutrient's data is to be shown to the user
                    LinkedHashMap<String, Float> nutrientMapToDisplay = new LinkedHashMap<>();

                    // Switch the map to be displayed according to current nutrient mode
                    switch (trackDietTab.nutrientLineChartMode) {
                        // Select appropriate maps for each mode
                        // And then break out of the switch statement
                        case ENERGY:
                            nutrientMapToDisplay = energyMap;
                            break;
                        case PROTEINS:
                            nutrientMapToDisplay = proteinsMap;
                            break;
                        case FATS:
                            nutrientMapToDisplay = fatsMap;
                            break;
                        case CARBOHYDRATES:
                            nutrientMapToDisplay = carbohydratesMap;
                            break;
                    }

                    // Set the line chart with appropriate nutrient map
                    trackDietTab.setLineChartWithDietIntakeData(nutrientMapToDisplay, context);

                } else {
                    // If data does not exists throw an error
                    Toast.makeText(context, "Error: Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch exception, show a toast error message and print error stack
                Toast.makeText(context, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });

    }

    /**
     * Saves the provided intake to the database
     *
     * @param context   Context to show toast message
     * @param ofyIntake Intake data to save
     */
    public static void saveMedicineIntake(HashMap<String, Integer> ofyIntake, Context context) {

        // Simple try catch block
        try {
            // Add medicine intake to proper location,
            // Database ref is already pointing current user
            ofyDatabaseref.child("Medicine").child(String.valueOf(LocalDate.now())).setValue(ofyIntake);

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error saving intake ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * Updates views with the prescription from the database
     *
     * @param context             Context to show toast message
     * @param medicineLayoutGroup Layout containing the views to update
     */
    public static void getPrescriptionAndUpdateViews(ViewGroup medicineLayoutGroup, Context context) {

        // Get the prescription from database and update the respective fields
        ofyDatabaseref.child("Medicine").child("Prescription").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Check if the data exists
                    if (snapshot.exists()) {

                        // Store the prescription coming from database
                        HashMap<String, Integer> prescription = (HashMap<String, Integer>) snapshot.getValue();

                        // Get the prescription's length (size)
                        int count = prescription.size();

                        // Loop through all the 5 medicine card views and hide or un-hide them accordingly
                        for (int i = 0; i <= 5; i++)

                            if (i <= count)
                                // If the current medicine index is less than medicines count in prescription
                                // Then make the card visible
                                ((ViewGroup) medicineLayoutGroup.getChildAt(i)).setVisibility(View.VISIBLE);

                            else
                                // Else hide the medicine card
                                ((ViewGroup) medicineLayoutGroup.getChildAt(i)).setVisibility(View.GONE);


                        // Iterate for each medicine prescription
                        for (HashMap.Entry<String, Integer> entry : prescription.entrySet()) {

                            // Get the linear layout of containing individual medicine's views by decrementing index
                            ViewGroup linearLayout = (ViewGroup) ((ViewGroup) medicineLayoutGroup.getChildAt(count--)).getChildAt(0);

                            // Set the medicine's name to the medicine's medicine name text view
                            ((TextView) linearLayout.getChildAt(1)).setText(entry.getKey());

                            // Get medicine's "field" text view
                            TextView medicineFieldTextView = (TextView) linearLayout.getChildAt(2);

                            // Get the current field text string from field
                            String newFieldText = medicineFieldTextView.getText().toString();

                            // Trim the text
                            newFieldText = newFieldText.substring(0, newFieldText.lastIndexOf(" "));


                            // Update the field text with the values form database
                            newFieldText += " " + entry.getValue();

                            // Set the field with the updated text
                            medicineFieldTextView.setText(newFieldText);

                        }

                        // Show a toast message on success
                        Toast.makeText(context, "Updated the prescription", Toast.LENGTH_SHORT).show();
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error in updating the prescription ", Toast.LENGTH_SHORT).show();
                    error.toException().printStackTrace();
            }
        });

    }


    /**
     * Gets the medicine intake from the database and adds it view in the form of cards to show it to the user
     *
     * @param context      Context to show toast message
     * @param linearLayout LinearLayout to add the card views with medicine intake data
     * @param date         Date to get the medicine intake of
     */
    public static void getMedicineAndUpdateViews(ViewGroup linearLayout, Activity context, String date) {

        // Get the medicine intake of the respective date from database and add the respective card views
        ofyDatabaseref.child("Medicine").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Simple try catch block
                try {

                    // Check if the data exists
                    if (snapshot.exists()) {

                        // Store the medicine intake coming from database
                        HashMap<String, Integer> medicineIntake = (HashMap<String, Integer>) snapshot.getValue();

                        // First remove all existing views
                        linearLayout.removeAllViews();

                        // If medicine intake is found then
                        if (medicineIntake != null) {

                            // Iterate through all the intake values
                            for (HashMap.Entry<String, Integer> entry : medicineIntake.entrySet()) {

                                // First get the linear layout surrounding the card
                                // This was added to have margins in the card view
                                ViewGroup linearLayoutSurroundingTheCard = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_medicine_card_layout, null);

                                // Now get the card view
                                ViewGroup cardView = (ViewGroup) linearLayoutSurroundingTheCard.getChildAt(0);

                                // Now set the medicine name by getting the entry's key
                                ((TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(0)).setText(entry.getKey());

                                // Now set the medicine intake by getting the entry's value
                                ((TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(1)).setText(entry.getValue() + " units");

                                // Now add the card to the layout provided earlier
                                linearLayout.addView(linearLayoutSurroundingTheCard, 0);

                            }
                        }
                    } else {

                        // Remove all medicine views
                        linearLayout.removeAllViews();

                        // Show a toast message if no medicine found
                        Toast.makeText(context, "No medicine intake found for this date", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error while updating medicine intake ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch exception, show a toast error message and print error stack
                Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });

    }

    /**
     * Updates the prescription in the database
     *
     * @param context             Context to show toast message
     * @param prescription        HashMap with the new prescription
     * */
    public static void setPrescription(HashMap<String, Integer> prescription, Context context) {

        // Simple try catch block
        try {
            // Add prescription to proper location,
            // Database ref is already pointing current user
            ofyDatabaseref.child("Medicine").child("Prescription").setValue(prescription);

            // Show a toast message on success
            Toast.makeText(context, "Updated the prescription", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error updating prescription ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * Updates all the other measures like water intake and weight in the database
     *
     * @param otherCounts HashMap with the data of all other measures
     * @param context    Context to show toast message
     */
    public static void saveOtherCounts(HashMap<String, Integer> otherCounts, Context context) {
        // Simple try catch block
        try {
            // Add values to proper location,
            // Database ref is already pointing current user
            ofyDatabaseref.child("Other").child(String.valueOf(LocalDate.now())).setValue(otherCounts);

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error updating other counts ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * Gets all the other measures like water and weight from database and sets the charts
     *
     * @param trackDietTab Track Diet tab object to call it's methods
     */
    public static void getOtherDataAndSetCharts(TrackDietTab trackDietTab) {
        // Get context for showing toast messages
        Context context = trackDietTab.getContext();

        // Query the required data, ordered by their keys
        Query query = ofyDatabaseref.child("Other").orderByKey().limitToLast(7);

        // Add a listener to listen to all changes to the data and show updated data to the user
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the data exists 
                if (snapshot.exists()) {

                    // Create linked hash maps to store the "other" data in an orderly manner
                    // Map to store water intake
                    LinkedHashMap<String, Integer> waterMap = new LinkedHashMap<>();

                    // Map to store weight data
                    LinkedHashMap<String, Integer> weightMap = new LinkedHashMap<>();

                    // Iterate for each date and get the data
                    for (DataSnapshot loggedDataForIndividualDate : snapshot.getChildren()) {

                        // Store the date, which is the key for this location
                        String key = loggedDataForIndividualDate.getKey();

                        // Get the logged "other" data in the form of a map
                        HashMap tempMap = (HashMap) loggedDataForIndividualDate.getValue();

                        // Store water intake data in the water map
                        waterMap.put(key, ((Long) tempMap.get("Water")).intValue());

                        // Store weight data in the  weight map
                        weightMap.put(key, ((Long) tempMap.get("Weight")).intValue());

                    }

                    // Set the bar chart with water intake data
                    trackDietTab.setBarChartWithWaterIntakeData(waterMap);

                    // Set the curved line chart with daily weight data
                    trackDietTab.setCurvedLineChartWithDailyWeightData(weightMap, context);

                } else {
                    // If data does not exists throw an error
                    Toast.makeText(context, "Error: Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch exception, show a toast error message and print error stack
                Toast.makeText(context, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();
            }
        });

    }
    /*
     * Gets today's all the other measures (like water intake) and update's views with logged data
     *
     * @param view         AddIntakeTab's view to update its other views
     * @param addIntakeTab AddIntakeTab for error message display
     */
    public static void getTodaysLoggedOtherMeasuresAndUpdateViews(View view, AddIntakeTab addIntakeTab) {

        // Context for showing toast messages
        Context context = addIntakeTab.getContext();

        // Simple try catch block
        try {

            // Get today's all the other user measures like weight and water intake and update views
            ofyDatabaseref.child("Other").child(String.valueOf(LocalDate.now())).get().addOnSuccessListener(task -> {

                // Simple try catch block
                try {

                    // Map to store all other saved measures
                    HashMap measures = (HashMap) task.getValue();

                    // If the measures contains water intake
                    if (measures.containsKey("Water")) {
                        // Then set today's water intake in the respective text view
                        ((TextView) view.findViewById(R.id.add_other_water_detail_label))
                                .setText(addIntakeTab.getString(R.string.Water_Intake, measures.get("Water")));
                    }
                    // If the saved measures contains logged weight
                    if (measures.containsKey("Weight")) {
                        // Then set today's recorded weight in the respective text view
                        ((TextView) view.findViewById(R.id.add_other_weight_detail_label))
                                .setText(addIntakeTab.getString(R.string.Weight_Logged, measures.get("Weight")));
                    }

                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error updating other measures", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            });
        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(context, "Error updating other measures", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
