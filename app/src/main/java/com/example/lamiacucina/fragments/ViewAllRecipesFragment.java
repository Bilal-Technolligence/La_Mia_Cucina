package com.example.lamiacucina.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.lamiacucina.R;
import com.example.lamiacucina.SelectRecipesActivity;
import com.example.lamiacucina.adapter.SelectRecipesAdaptor;
import com.example.lamiacucina.adapter.ViewAllRecipesAdaptor;
import com.example.lamiacucina.model.Ingredient;
import com.example.lamiacucina.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllRecipesFragment extends Fragment {
    public FirebaseAuth mAuth;
    ArrayList<Recipe> currentList;
    ViewAllRecipesAdaptor md;
    RecyclerView rv;
    View NoRecordFoundView;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    public ViewAllRecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_recipes, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressBar = view.findViewById(R.id.progressBar);
        NoRecordFoundView = view.findViewById(R.id.noRcdFnd);
        NoRecordFoundView.setVisibility(View.GONE);

        rv = view.findViewById(R.id.recyclerViewViewAllRecipes);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(requireActivity());
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
                            recipe.addIngredient(ingredients.getValue(Ingredient.class));
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
                        md = new ViewAllRecipesAdaptor(requireActivity(), currentList, false);
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

        return view;
    }
}