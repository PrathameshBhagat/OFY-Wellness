package com.ofywellness.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.ofywellness.HomeActivity;
import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.User;

public class RegisterActivity extends AppCompatActivity {
    static int USER_AGE;
    static int USER_WEIGHT;
    static int USER_HEIGHT;
    static ViewPager2 registerViewPager2;
    static String USER_EMAIl;
    static String USER_FIRST_NAME;
    static String USER_LAST_NAME;
    static String USER_PHONE;
    static String USER_GENDER;

    static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ProgressBar progressBar = findViewById(R.id.register_progressBar);

        c= RegisterActivity.this;
        registerViewPager2 = findViewById(R.id.register_view_pager);

        // Set adapter to tab viewer
        registerViewPager2.setAdapter(new RegisterTabAdapter(this));

        registerViewPager2.setUserInputEnabled(false);

        registerViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                progressBar.setProgress((int) (((1 + position) / (float)5) * 100));
            }
        });

    }

    static void incrementTab() {

        registerViewPager2.setCurrentItem(registerViewPager2.getCurrentItem() + 1);

    }

    static void registerUser() {

        // Simple try catch block
        try {

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(c);

            // Create new User to add to database
            User ofyNewUser = new User(account.getEmail(),
                    account.getGivenName(),
                    account.getFamilyName(),
                    RegisterActivity.USER_PHONE,
                    RegisterActivity.USER_GENDER,
                    RegisterActivity.USER_AGE,
                    RegisterActivity.USER_WEIGHT,
                    RegisterActivity.USER_HEIGHT
            );

            // Add user to the database and get UserID
            String userID = ofyDatabase.addNewUserToFirebase(ofyNewUser, c);
            // If userID is not empty
            if (!userID.isEmpty()) {
                // Create intent to move to next activity and provide it the UserID
                Intent nextActivity = new Intent(c, HomeActivity.class);
                nextActivity.putExtra("ID", userID);
                // Start the Intent
                c.startActivity(nextActivity);
            }

        } catch(Exception e){
            Toast.makeText(c, "Unable to register user, please close the app and try again", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



}