package com.ofywellness.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ofywellness.modals.User;

public class ofyDatabase {
    private static DatabaseReference ofyDatabaseref;

    public static void addUser(User ofyUser){
        ofyDatabaseref = FirebaseDatabase.getInstance().getReference();
        ofyDatabaseref.child("Users").push().setValue(ofyUser);
    }

    public void addMeal(){}

}
