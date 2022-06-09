package com.example.lamiacucina.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lamiacucina.AddIngredientKitchenLogActivity;
import com.example.lamiacucina.R;
import com.example.lamiacucina.ViewIngredientKitchenLogActivity;

public class KitchenLogFragment extends Fragment {

    CardView addIngredient,viewIngredient;

    public KitchenLogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kitchen_log, container, false);

        addIngredient = view.findViewById(R.id.addIngredient);
        viewIngredient = view.findViewById(R.id.viewIngredient);

        addIngredient.setOnClickListener(view1 -> startActivity(new Intent(getContext(), AddIngredientKitchenLogActivity.class)));
        viewIngredient.setOnClickListener(view1 -> startActivity(new Intent(getContext(), ViewIngredientKitchenLogActivity.class)));

        return view;
    }
}