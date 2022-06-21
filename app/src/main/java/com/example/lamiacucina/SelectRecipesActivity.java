package com.example.lamiacucina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lamiacucina.adapter.SelectRecipesAdaptor;
import com.example.lamiacucina.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectRecipesActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    ArrayList<Recipe> currentList;
    SelectRecipesAdaptor md;
    RecyclerView rv;
    View NoRecordFoundView;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    ImageView SaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipes);

        Intent intent = getIntent();
        HashMap<String, String> data = (HashMap<String, String>) intent.getSerializableExtra("data");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressBar = findViewById(R.id.progressBar);
        NoRecordFoundView = findViewById(R.id.noRcdFnd);
        SaveData = findViewById(R.id.SaveData);
        NoRecordFoundView.setVisibility(View.GONE);

        SaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Recipe> recipes = GetRecipesSelection();

                Toast.makeText(SelectRecipesActivity.this, "Meal Saved !!!", Toast.LENGTH_SHORT).show();
                finish();
            }

            ArrayList<Recipe> GetRecipesSelection() {
                ArrayList<Recipe> recipes = md.GetRecipesSelected();
                if (recipes == null) {
                    Toast.makeText(SelectRecipesActivity.this, "Please Select Recipes first", Toast.LENGTH_SHORT).show();
                    return null;
                } else
                    return recipes;
            }
        });

        rv = findViewById(R.id.recyclerViewSelectRecipes);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(SelectRecipesActivity.this);
        rv.setLayoutManager(rlm);

        mAuth = FirebaseAuth.getInstance();

        currentList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child("Recipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eachAdRecord : dataSnapshot.getChildren()) {
                        Recipe recipe = new Recipe();
                        recipe.setID(eachAdRecord.getKey());
                        recipe.setTitle(eachAdRecord.child("title").getValue(String.class));
                        recipe.setImage(eachAdRecord.child("Image").getValue(String.class));
                        recipe.setInstruction(eachAdRecord.child("Instructions").getValue(String.class));
                        recipe.setSelected(false);

                        for (DataSnapshot ingredients : eachAdRecord.child("ingredients").getChildren()) {
                            recipe.addIngredient(ingredients.getValue(String.class));
                        }

                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                        currentList.add(recipe);
                    }
                    progressBar.setVisibility(View.GONE);
                    if (!currentList.isEmpty()) {
                        NoRecordFoundView.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        md = new SelectRecipesAdaptor(SelectRecipesActivity.this, currentList, false);
                        rv.setAdapter(md);
                    } else {
                        NoRecordFoundView.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
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