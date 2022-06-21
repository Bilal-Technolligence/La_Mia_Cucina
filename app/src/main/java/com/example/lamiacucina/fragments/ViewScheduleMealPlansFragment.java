package com.example.lamiacucina.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lamiacucina.PantryManagerActivity;
import com.example.lamiacucina.R;
import com.google.android.material.navigation.NavigationBarView;

public class ViewScheduleMealPlansFragment extends Fragment {

    public ViewScheduleMealPlansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_view_schedule_meal_plans, container, false);

         return view;
    }
}