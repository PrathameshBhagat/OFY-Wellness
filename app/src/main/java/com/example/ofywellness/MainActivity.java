package com.example.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class MainActivity extends  AppCompatActivity {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private Button login,register;
    private SignInButton google;
    private EditText emailfield,passfield;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailfield=(EditText)findViewById(R.id.email);
        passfield=(EditText)findViewById(R.id.password);
        register = (Button) findViewById(R.id.registerbutton);
        login = (Button) findViewById(R.id.button);
        google = (SignInButton) findViewById(R.id.google_image);
        gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc =GoogleSignIn.getClient(this,gso);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent=gsc.getSignInIntent();
                startActivityForResult(signInIntent,
                        1000);

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(emailfield.getText().toString(), passfield.getText().toString());}
            });
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Register(emailfield.getText().toString(), passfield.getText().toString());
            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                finish();
                Toast.makeText(getApplicationContext(), "Logged In",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            } catch (ApiException e) {
                System.out.println("****************************************************************************");
                System.out.println(e.getMessage()+e.toString());e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Register(String username, String pass){
        if(username==""||username.trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Usename Empty !", Toast.LENGTH_SHORT).show();
            return;}
        else if(pass==""||pass.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Password Empty !", Toast.LENGTH_SHORT).show();
            return;
        }
        else
            try {
                File data = new File(this.getFilesDir() + File.separator + "data.txt");
                if(!data.exists())data.createNewFile();
                FileWriter fw = new FileWriter(data);
                fw.append(username+','+pass+"0");
                fw.flush();
                fw.close();
                Toast.makeText(getApplicationContext(), "Registered ", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
            }
    }
    public void Login(String username,String pass){
        if(username==""||username.trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Usename Empty !", Toast.LENGTH_SHORT).show();
            return;}
        else if(pass==""||pass.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Password Empty !", Toast.LENGTH_SHORT).show();
                return;
        }
        else
           try {
               File data = new File(this.getFilesDir()+File.separator+"data.txt");
               Scanner sc = new Scanner(data);
               StringBuilder sb = new StringBuilder();
               boolean log = false;
               while (sc.hasNextLine()) {
                   String l=sc.nextLine();
                   sb.append( l+ System.lineSeparator());
                   if (l.contains(username)) {
                       if (l.contains("," + pass + "0")) {
                           log=true;
                           Intent move = new Intent(MainActivity.this, Home.class);
                           startActivity(move);
                           Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
                       } else if (l.contains("," + pass)&&!l.contains("," + pass + "0"))
                           Toast.makeText(getApplicationContext(), "User Already Logged in ", Toast.LENGTH_SHORT).show();
                       else {
                           Toast.makeText(getApplicationContext(), "Wrong Pass Word ", Toast.LENGTH_SHORT).show();
                       }
                   }
               }
               if (log)
               {
                   FileWriter fw=new FileWriter(data);
                   fw.write(sb.toString().replace(username+","+pass+"0",username+","+pass+"1"));
                   fw.flush();
                   fw.close();
               }
           }
           catch (Exception e){
               Toast.makeText(getApplicationContext(),"Error Logging : "+e.getMessage() , Toast.LENGTH_LONG).show();
           }
    }
}