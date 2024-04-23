package com.ofywellness.db;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.ofywellness.modals.Meal;
import com.ofywellness.modals.User;

import java.util.Date;

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

    public static void trackDiet() {
        ofyDatabaseref.child("Diet").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
}
