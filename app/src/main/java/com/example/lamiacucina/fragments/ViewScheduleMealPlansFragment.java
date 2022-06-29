package com.example.lamiacucina.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lamiacucina.R;
import com.example.lamiacucina.adapter.ViewAllPlannedMealsAdaptor;
import com.example.lamiacucina.adapter.ViewAllRecipesAdaptor;
import com.example.lamiacucina.model.Ingredient;
import com.example.lamiacucina.model.Meal;
import com.example.lamiacucina.model.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewScheduleMealPlansFragment extends Fragment {
    public FirebaseAuth mAuth;
    ArrayList<Meal> currentList;
    ViewAllPlannedMealsAdaptor md;
    RecyclerView rv;
    View NoRecordFoundView;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    public ViewScheduleMealPlansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_schedule_meal_plans, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressBar = view.findViewById(R.id.progressBar);
        NoRecordFoundView = view.findViewById(R.id.noRcdFnd);
        NoRecordFoundView.setVisibility(View.GONE);

        rv = view.findViewById(R.id.recyclerViewViewMealsPlan);
        RecyclerView.LayoutManager rlm = new LinearLayoutManager(requireActivity());
        rv.setLayoutManager(rlm);

        mAuth = FirebaseAuth.getInstance();

        currentList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference().child("Meals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eachAdRecord : dataSnapshot.getChildren()) {
                        Meal meal = new Meal();
                        meal.setID(eachAdRecord.getKey());
                        meal.setMealTime(eachAdRecord.child("MealTime").getValue(String.class));
                        meal.setDurationOfMeal(eachAdRecord.child("DurationOfMeal").getValue(String.class));
                        meal.setMenuName(eachAdRecord.child("MenuName").getValue(String.class));
                        meal.setServing(eachAdRecord.child("Serving").getValue(String.class));

                        ArrayList<String> RecipesList = new ArrayList<>();
                        for (DataSnapshot recipes : eachAdRecord.child("Recipes").getChildren()) {
                            RecipesList.add(recipes.getValue(String.class));
                        }
                        meal.setRecipesID(RecipesList);

                        currentList.add(meal);
                    }
                    progressBar.setVisibility(View.GONE);
                    if (!currentList.isEmpty()) {
                        NoRecordFoundView.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        md = new ViewAllPlannedMealsAdaptor(requireActivity(), currentList, false);
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