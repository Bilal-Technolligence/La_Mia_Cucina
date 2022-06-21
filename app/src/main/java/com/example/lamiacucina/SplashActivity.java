package com.example.lamiacucina;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        if (snapshot.child("Role").exists())
                        {
                            String Role = snapshot.child("Role").getValue(String.class);
                            if (Objects.requireNonNull(Role).equals("Chef"))
                                startActivity(new Intent(SplashActivity.this, ChefActivity.class));
                            else if (Role.equals("Pantry Manager"))
                                startActivity(new Intent(SplashActivity.this, PantryManagerActivity.class));
                            else
                                startActivity(new Intent(SplashActivity.this, StartActivity.class));
                        }
                        else{
                            startActivity(new Intent(SplashActivity.this, StartActivity.class));
                        }
                    }
                    else{
                        startActivity(new Intent(SplashActivity.this, StartActivity.class));
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}