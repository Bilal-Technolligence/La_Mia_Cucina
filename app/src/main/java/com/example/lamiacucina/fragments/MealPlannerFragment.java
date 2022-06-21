package com.example.lamiacucina.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.lamiacucina.R;
import com.example.lamiacucina.SelectRecipesActivity;
import java.util.HashMap;

public class MealPlannerFragment extends Fragment {
    Button SelectRecipeButton;
    EditText MenuName,DurationOfMeal,MealTime;

    public MealPlannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_planner, container, false);
        SelectRecipeButton = view.findViewById(R.id.SelectRecipeButton);
        MenuName = view.findViewById(R.id.MenuName);
        DurationOfMeal = view.findViewById(R.id.DurationOfMeal);
        MealTime = view.findViewById(R.id.MealTime);

        SelectRecipeButton.setOnClickListener(view1 -> {
            HashMap<String,String> data = GetData();
            if (data!=null) {
                startActivity(new Intent(requireActivity(), SelectRecipesActivity.class).putExtra("data", data));
                MenuName.setText("");
                DurationOfMeal.setText("");
                MealTime.setText("");
            }else
                Toast.makeText(requireActivity(), "Please fill all required Data", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private HashMap<String, String> GetData() {
        HashMap<String, String> data = new HashMap<>();

        String menuName;
        String durationOfMeal;
        String mealTime;

        if (MenuName.getText() !=null && !MenuName.getText().toString().equals(""))
            menuName = MenuName.getText().toString();
        else
        {
            MenuName.setError("Please Add Menu Name");
            MenuName.requestFocus();
            return null;
        }

        if (DurationOfMeal.getText() !=null && !DurationOfMeal.getText().toString().equals(""))
            durationOfMeal = DurationOfMeal.getText().toString();
        else
        {
            DurationOfMeal.setError("Please Add Meal Duration");
            DurationOfMeal.requestFocus();
            return null;
        }

        if (MealTime.getText() !=null && !MealTime.getText().toString().equals(""))
            mealTime = MealTime.getText().toString();
        else
        {
            MealTime.setError("Please Add Meal Time");
            MealTime.requestFocus();
            return null;
        }


        data.put("MenuName",menuName);
        data.put("DurationOfMeal",durationOfMeal);
        data.put("MealTime",mealTime);

        return data;
    }
}