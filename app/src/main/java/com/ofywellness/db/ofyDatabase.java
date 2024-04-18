package com.ofywellness.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

}
