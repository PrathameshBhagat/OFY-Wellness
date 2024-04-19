package com.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/* TODO : Comments required and renaming and remove images from drawable */
public class Category extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
    }

    public void returnhome(View view) {
        Intent category = new Intent(Category.this, HomeActivity.class);
        finishAffinity();
        startActivity(category.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    public void next(View view) {
        Intent category = new Intent(Category.this, MeetActivity.class);
        startActivity(category.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }
}