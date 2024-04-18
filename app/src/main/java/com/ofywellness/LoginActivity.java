package com.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInClient ofyGoogleSignInClient;
    private DatabaseReference ofyDBReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get database reference
        ofyDBReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Make the user signIn to his google account
        signInToGoogleAccount();

    }
    void signInToGoogleAccount(){
        //Implementing Google Sign-In

        // Objects required
        SignInButton ofyGoogleSignInButton = findViewById(R.id.google_image);
        GoogleSignInOptions ofyGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        ofyGoogleSignInClient = GoogleSignIn.getClient(this, ofyGoogleSignInOptions);

        //onClick for Google Sign-In Button
        ofyGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = ofyGoogleSignInClient.getSignInIntent();
                // This intent will make user to signIn to his account,
                // and  control will move onActivityResult method
                startActivityForResult(signInIntent, 1000);
            }
        });
    }

    // To handle Google Sign-In result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the requested code is same as given before
        if (requestCode == 1000) {
            try {
                //Get Signed Account
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
                // Finish current activity
                finish();
                // Show a Toast message
                Toast.makeText(getApplicationContext(), "Google account: " + account.getDisplayName(), Toast.LENGTH_SHORT).show();

                // Move to next activity on success
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            } catch (Exception e) {
                //Log Error
                Log.e(" Error ", "Error Signing in to Google *******************************************************************");
                // Print full Error
                e.printStackTrace();
                // Show a Toast message
                Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}