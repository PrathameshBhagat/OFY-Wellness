package com.ofywellness.db;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.ofywellness.modals.Meal;
import com.ofywellness.modals.User;

import java.util.Date;
import java.util.HashMap;

// Single class for all database operations
public class ofyDatabase {
    private static DatabaseReference ofyDatabaseref;

    public static String addUser(User ofyUser) {
        // Get database reference
        ofyDatabaseref = FirebaseDatabase.getInstance().getReference();
        // Set operation to push to automatically get unique UserID with a storage location
        ofyDatabaseref = ofyDatabaseref.child("Users").push();
        // Add the user to database
        ofyDatabaseref.setValue(ofyUser);
        // Return the UserID
        return ofyDatabaseref.getKey();
    }

    public static void addMeal(Meal ofyMeal, String mealType) {

        // Add meal to proper location,
        // Database ref is already pointing current user
        ofyDatabaseref.child("Diet").child(String.valueOf(new Date())).child(mealType).setValue(ofyMeal);

    }

    public static void getTrackDietDataAndSetView(Context c, TextView energyValueLabel, TextView proteinsValueLabel, TextView fatsValueLabel, TextView carbohydratesValueLabel) {

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

                    // Show a toast message
                    Toast.makeText(c, "Updated the data", Toast.LENGTH_SHORT).show();

                } else {
                    // Show a toast error message
                    Toast.makeText(c, "Error getting data from firebase", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
