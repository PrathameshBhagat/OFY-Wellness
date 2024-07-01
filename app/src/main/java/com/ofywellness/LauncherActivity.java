package com.ofywellness;

import static com.ofywellness.db.ofyDatabase.findUserInFirebaseAndNext;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view for this activity
        setContentView(R.layout.activity_launcher);

        // Get the background animation from the main layout
        AnimationDrawable animationDrawable = (AnimationDrawable) findViewById(R.id.launcher_relative_layout).getBackground();

        // Set the fade in and fade out durations for the animation
        animationDrawable.setEnterFadeDuration(400);
        animationDrawable.setExitFadeDuration(400);

        // Start the animation
        animationDrawable.start();

        // Now look for an google account which user signed in last time
        // And attempt to login to it

        // Look for the google account which user already logged in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Check if no past account was found
        if ( account == null)
            // If no account fount start the LoginActivity to make user login
            startActivity(new Intent(this, LoginActivity.class));

        else
            // Else If a google account was found look for the account in database
            // And move to next activity
            findUserInFirebaseAndNext(this, account.getEmail());

    }
}