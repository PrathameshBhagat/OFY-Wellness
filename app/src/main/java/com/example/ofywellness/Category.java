package com.example.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Category extends AppCompatActivity {
    ImageView profile;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        /* his code was used to add profile log out button  on click of image button which is removed
        profile= findViewById(R.id.profile);
        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount accnt=GoogleSignIn.getLastSignedInAccount(this);
        profile.setOnClickListener(view1 -> {
            gsc.signOut().addOnCompleteListener(task -> {
                finish();finishAffinity();
                startActivity(new Intent(Category.this,MainActivity.class));
                Toast.makeText(getApplicationContext(),"Logged Out !, Signin Again ",Toast.LENGTH_SHORT).show();

            });
        });*/
    }

    public void returnhome(View view) {
        Intent category= new Intent(Category.this,Home.class);
        finishAffinity();
        startActivity(category.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
    public void next(View view) {
        Intent category= new Intent(Category.this,MeetActivity.class);
        startActivity(category.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }
}