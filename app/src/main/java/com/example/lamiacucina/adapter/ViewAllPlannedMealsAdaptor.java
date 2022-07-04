package com.example.lamiacucina.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lamiacucina.R;
import com.example.lamiacucina.model.Meal;

import java.util.ArrayList;

public class ViewAllPlannedMealsAdaptor extends RecyclerView.Adapter<ViewAllPlannedMealsAdaptor.MyHolder> {
    Context ct;
    ArrayList<Meal> al;
    Boolean Editable;

    public ViewAllPlannedMealsAdaptor(Context cont, ArrayList<Meal> al, Boolean editable) {
        this.ct = cont;
        this.al = al;
        Editable = editable;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(ct);
        View v = li.inflate(R.layout.recycler_view_planned_meals_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAllPlannedMealsAdaptor.MyHolder holder, final int position) {
        holder.bind(al.get(position),position);
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView MenuName,DurationOfMeal,MealTime,MealType,Serving,Recipes;
        View cld;

        public MyHolder(View itemView) {
            super(itemView);
            cld = itemView.findViewById(R.id.PlannedMealsCard);

            MenuName = itemView.findViewById(R.id.MenuName);
            DurationOfMeal = itemView.findViewById(R.id.DurationOfMeal);
            MealTime = itemView.findViewById(R.id.MealTime);
            MealType = itemView.findViewById(R.id.MealType);
            Serving = itemView.findViewById(R.id.Serving);
            Recipes = itemView.findViewById(R.id.Recipes);
        }

        public void bind(final Meal p1,int position) {
            MenuName.setText("Menu Name : " + p1.getMenuName());
            DurationOfMeal.setText("DurationOfMeal : " + p1.getDurationOfMeal());
            MealTime.setText("Meal Time : " + p1.getMealTime());
            MealType.setText("Meal Type : " + p1.getMenuType());
            Serving.setText("Serving : " + p1.getServing());
            Recipes.setText("Recipes include : " + p1.getRecipesID().size());

            //cld.setOnClickListener(view -> ct.startActivity(new Intent(ct, PlannedMealDetailActivity.class).putExtra("Meal", p1)));
        }
    }
}