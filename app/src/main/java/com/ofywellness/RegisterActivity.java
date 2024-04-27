package com.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.User;
/* TODO : Comments required and renaming and remove images from drawable */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get all the required objects
        EditText firstNameText,lastNameText,phoneText,genderText,AgeText,weightText,heightText;
        firstNameText = findViewById(R.id.firstname);
        lastNameText = findViewById(R.id.lastname);
        phoneText = findViewById(R.id.phonenumber);
        genderText = findViewById(R.id.gender);
        AgeText = findViewById(R.id.age);
        weightText = findViewById(R.id.weight);
        heightText = findViewById(R.id.height);

        // Create new user in database onclick of button
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create new User to add to database
                User ofyNewUser = new User(getIntent().getStringExtra("email"),
                        firstNameText.getText().toString(),
                        lastNameText.getText().toString(),
                        phoneText.getText().toString(),
                        genderText.getText().toString(),
                        Integer.parseInt(AgeText.getText().toString()),
                        Integer.parseInt(weightText.getText().toString()),
                        Integer.parseInt(heightText.getText().toString())
                );

                // Add user to the database and get UserID
                String userID = ofyDatabase.addNewUserToFirebaseDatabase(ofyNewUser);
                // Create intent to move to next activity and provide it the UserID
                Intent nextActivity = new Intent(RegisterActivity.this,HomeActivity.class);
                nextActivity.putExtra("ID",userID);
                // Start the Intent
                startActivity(nextActivity);

            }
        });

    }
}