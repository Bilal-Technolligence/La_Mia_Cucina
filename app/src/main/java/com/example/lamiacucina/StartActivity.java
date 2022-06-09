package com.example.lamiacucina;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    Button btnLogin,btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btnSignup =findViewById(R.id.registerBtn);
        btnLogin =findViewById(R.id.btnLogin);

        btnSignup.setOnClickListener(view -> {
            Intent i= new Intent(StartActivity.this, Signup.class);
            startActivity(i);
            finish();
        });
        btnLogin.setOnClickListener(view -> {
            Intent i= new Intent(StartActivity.this, Login.class);
            startActivity(i);
            finish();
        });
    }
}