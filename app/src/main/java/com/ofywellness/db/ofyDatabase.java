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

import java.util.Date;
import java.util.HashMap;

// Single class for all database operations
public class ofyDatabase {
    private static DatabaseReference ofyDatabaseref;

    /**
     * Add new user to Firebase Database
     *
     * @param ofyUser The user data model object
     * @return The auto-generated UserID of this user
     */
    public static String addNewUserToFirebaseDatabase(User ofyUser) {
        // Get database reference
        ofyDatabaseref = FirebaseDatabase.getInstance().getReference();
        // Set operation to push to automatically get unique UserID with a storage location
        ofyDatabaseref = ofyDatabaseref.child("Users").push();
        // Add the user to database
        ofyDatabaseref.setValue(ofyUser);
        // Return the UserID
        return ofyDatabaseref.getKey();
    }

    /**
     * Get UserID from  Firebase Database if user exists
     * @param ofyUserEmail The email of current user to find in database
     */
    public static void findUserInFirebaseAndNext(Context context, String ofyUserEmail) {

        // Get the Firebase Database reference to users
        ofyDatabaseref = FirebaseDatabase.getInstance().getReference().child("Users");

        // Get the users data and add on complete listener to run method on obtaining data
        ofyDatabaseref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {

                // boolean to check if we moved to next activity
                boolean moved = false;

                // if task is successful move forward
                if (task.isSuccessful()) {

                    // Loop through all individual users
                    for (DataSnapshot individualUser : task.getResult().getChildren()) {

                        // Get individual users details and map to a hashmap
                        HashMap userDetail = (HashMap) individualUser.getValue();

                        // If user detail has email field with value equal to the obtained email
                        // Then finish current activity and move to next activity
                        if (userDetail.containsKey("email") && userDetail.get("email").toString().equals(ofyUserEmail)) {

                            ((Activity) context).finish();
                            startActivity(context, new Intent(context, HomeActivity.class).putExtra("UserID", individualUser.getKey()), null);

                            // Also  set this boolean to indicate we moved to next activity and break the loop
                            moved = true;
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
            }
        });

    }

    /**
     * Add meal to user's diet in Firebase Database
     *
     * @param ofyMeal  The meal details
     * @param mealType The type of meal breakfast, lunch, dinner
     */
    public static void addMeal(Meal ofyMeal, String mealType) {

        // Add meal to proper location,
        // Database ref is already pointing current user
        ofyDatabaseref.child("Diet").child(String.valueOf(new Date())).child(mealType).setValue(ofyMeal);

    }
    /**
     * Track user's  diet and set TextViews to the tracking data
     *
     * @param context The context to show toast message
     * @param energyValueLabel All others are TextViews and Progress Bars to set tracking data
     */
    public static void getTrackDietDataAndSetData(Context context, TextView energyValueLabel, TextView proteinsValueLabel, TextView fatsValueLabel, TextView carbohydratesValueLabel, ProgressBar energyProgressBar, ProgressBar proteinsProgressBar,ProgressBar fatsProgressBar,ProgressBar carbohydratesProgressBar) {

        // Get the DataSnapshot to get tracking data, calculate it
        // And display it to user by updating the text views
        ofyDatabaseref.child("Diet").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {
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

                    // Calculate total intake of nutrients for progress bars inputs
                    int total = Integer.parseInt(energyValueLabel.getText().toString().replace("Cal", ""));
                    total += Integer.parseInt(proteinsValueLabel.getText().toString().replace("g", ""));
                    total += Integer.parseInt(fatsValueLabel.getText().toString().replace("g", ""));
                    total += Integer.parseInt(carbohydratesValueLabel.getText().toString().replace("g", ""));

                    // Set total 1 to avoid zivision by zero if no data present
                    total = (total == 0) ? 1 : total;

                    // Set the progress bars to proper percentage values of data they represent
                    energyProgressBar.setProgress(Integer.parseInt(energyValueLabel.getText().toString().replace("Cal", "")) * 100 / total);

                    proteinsProgressBar.setProgress(Integer.parseInt(proteinsValueLabel.getText().toString().replace("g", "")) * 100 / total);

                    fatsProgressBar.setProgress(Integer.parseInt(fatsValueLabel.getText().toString().replace("g", "")) * 100 / total);

                    carbohydratesProgressBar.setProgress(Integer.parseInt(carbohydratesValueLabel.getText().toString().replace("g", "")) * 100 / total);

                    // Show a toast message
                    Toast.makeText(context, "Updated the data", Toast.LENGTH_SHORT).show();
                } else {
                    // Show a toast error message
                    Toast.makeText(context, "Error getting data from firebase", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
