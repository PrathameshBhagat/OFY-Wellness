package com.ofywellness.db;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.ofywellness.HomeActivity;
import com.ofywellness.RegisterActivity;
import com.ofywellness.modals.Meal;
import com.ofywellness.modals.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
            // Return the UserID
            return ofyDatabaseref.getKey();

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
        ofyDatabaseref = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get the users data and add on complete listener to run method on obtaining data
        ofyDatabaseref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                ofyDatabaseref = individualUser.getRef();
                                // And move to next activity and provide UserID (currently has no usage)
                                startActivity(context, new Intent(context, HomeActivity.class).putExtra("UserID", individualUser.getKey()), null);

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
     * @param context          The context to show toast message
     * @param energyValueLabel All others are TextViews and Progress Bars to set tracking data
     */
    public static void getTrackDietDataAndSetData(Context context, TextView energyValueLabel, TextView proteinsValueLabel, TextView fatsValueLabel, TextView carbohydratesValueLabel) {

        // Get the DataSnapshot to get tracking data, calculate it
        // And display it to user by updating the text views
        ofyDatabaseref.child("Diet").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {
                try {
                    // Check if the task to get data was successful
                    if (task.isSuccessful()) {
                        // Tracking data variables
                        int totalEnergy = 0, totalProteins = 0, totalFats = 0, totalCarbohydrates = 0;

                        // Loop through all the days
                        for (DataSnapshot ofyDateDataSnapshot : task.getResult().getChildren()) {
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

                        // Show a toast message
                        Toast.makeText(context, "Updated the data", Toast.LENGTH_SHORT).show();
                    } else {
                        // Show a toast error message
                        Toast.makeText(context, "Error getting data from firebase", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error in getting and setting data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
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
    public static void updateDietProgress(Context context, Meal currentProgress, ProgressBar energyProgressBar, ProgressBar proteinsProgressBar, ProgressBar fatsProgressBar, ProgressBar carbohydratesProgressBar) {

        // Database ref is already pointing current user just get the target and update progress
        ofyDatabaseref.child("Target").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {
                // Simple try catch block
                try {
                    // Check if the task to get data was successful
                    if (task.isSuccessful()) {
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
                        HashMap target = (HashMap) task.getResult().getValue();

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

                        // Show a toast message
                        Toast.makeText(context, "Updated the progress", Toast.LENGTH_SHORT).show();
                    } else {
                        // Show a toast error message
                        Toast.makeText(context, "Error getting data from firebase", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error in getting and setting data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
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
    public static void setMealsOfTheDay(Context context, int year, int month, int dayOfMonth) {

        // Initialise the ArrayList object
        allMealsFound = new ArrayList<>();

        // Get all the meals eaten on the date provided and them to the ArrayList
        ofyDatabaseref.child("Diet").child(LocalDate.of(year, month + 1, dayOfMonth).toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(Task<DataSnapshot> task) {

                // Simple try catch block
                try {
                    // Check if the task to get data was successful
                    if (task.isSuccessful()) {

                        // Loop through all the days
                        for (DataSnapshot ofyDateDataSnapshot : task.getResult().getChildren()) {

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

                            // Add all the meals to later retrieve them received
                            allMealsFound.add(receivedMeal);

                        }

                    } else {
                        // Show a toast error message
                        Toast.makeText(context, "Error, Please set the date again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Catch exception, show a toast error message and print error stack
                    Toast.makeText(context, "Error, Please set the date again", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     *   Get all the meals eaten in a day which was stored earlier */
    public static ArrayList<Meal> getMeals() {

        return allMealsFound;

    }
}
