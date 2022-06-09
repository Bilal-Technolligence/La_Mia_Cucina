package com.example.lamiacucina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.lamiacucina.adapter.IngredientListAdaptor;
import com.example.lamiacucina.model.Ingredient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ViewIngredientKitchenLogActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    ArrayList<Ingredient> al;
    IngredientListAdaptor md;
    RecyclerView rv;
    View NoRecordFoundView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ingredient_kitchen_log);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        NoRecordFoundView = findViewById(R.id.noRcdFnd);
        NoRecordFoundView.setVisibility(View.GONE);

        rv = findViewById(R.id.recyclerViewIngredients);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(this);
        rv.setLayoutManager(rlm);

        mAuth = FirebaseAuth.getInstance();

        al = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eachAdRecord : dataSnapshot.getChildren()) {
                        Ingredient p = new Ingredient();
                        p.setID(eachAdRecord.getKey());

                        p.setIngredientName(eachAdRecord.child("IngredientName").getValue(String.class));
                        p.setIngredientQuantity(eachAdRecord.child("IngredientQuantity").getValue(String.class));
                        p.setIngredientUnit(eachAdRecord.child("IngredientUnit").getValue(String.class));
                        p.setIngredientThresholdValue(eachAdRecord.child("IngredientThresholdValue").getValue(String.class));

                        al.add(p);
                    }
                    if (!al.isEmpty()) {
                        NoRecordFoundView.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        md = new IngredientListAdaptor(ViewIngredientKitchenLogActivity.this, al);
                        rv.setAdapter(md);
                    } else {
                        NoRecordFoundView.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }
                } else {
                    NoRecordFoundView.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}