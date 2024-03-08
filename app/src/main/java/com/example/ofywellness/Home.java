package com.example.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Home extends AppCompatActivity {
    ImageButton profile;
    TextView tname;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profile= findViewById(R.id.profile);
        tname= findViewById(R.id.name);
        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc=GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount accnt=GoogleSignIn.getLastSignedInAccount(this);
        if(accnt!=null)
        {  String name=accnt.getDisplayName();
            if(name.equals(""))name="Hello There !";
            tname.setText(name);
        }
        profile.setOnClickListener(view1 -> {
            gsc.signOut().addOnCompleteListener(task -> {
                finish();finishAffinity();
                startActivity(new Intent(Home.this,MainActivity.class));
                Toast.makeText(getApplicationContext(),"Logged Out !, Signin Again ",Toast.LENGTH_SHORT).show();
            });
        });
    }
    public void categorystart(View view) {
        Intent home= new Intent(Home.this,Category.class);
        startActivity(home.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));

    }
}