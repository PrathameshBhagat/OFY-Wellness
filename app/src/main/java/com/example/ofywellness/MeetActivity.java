package com.example.ofywellness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MeetActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);
        requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO"},80);
        startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://prathameshbhagat.000webhostapp.com/LCam/")));
        Toast.makeText(this,"MeetingCreated",Toast.LENGTH_SHORT).show();
    }
    public void returnhome(View view) {
        Intent category= new Intent(MeetActivity.this,Category.class);
        finishAffinity();
        startActivity(category.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

    public void restart(View view) {
        startActivity(new Intent (Intent.ACTION_VIEW, Uri.parse("https://prathameshbhagat.000webhostapp.com/LCam/")));
        Toast.makeText(this,"MeetingCreated",Toast.LENGTH_LONG).show();
    }
}