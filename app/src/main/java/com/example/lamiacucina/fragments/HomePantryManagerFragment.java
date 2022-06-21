package com.example.lamiacucina.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.lamiacucina.PantryManagerActivity;
import com.example.lamiacucina.R;
import com.example.lamiacucina.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePantryManagerFragment extends Fragment {
    CircleImageView ProfileImage;
    TextView UserNameTxt;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FragmentManager fragmentManager;
    CardView SeePlannedMeals;

    public HomePantryManagerFragment(FragmentManager supportFragmentManager) {
        // Required empty public constructor
        fragmentManager = supportFragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_pantry_manager, container, false);

        ProfileImage = view.findViewById(R.id.profile_image);
        UserNameTxt = view.findViewById(R.id.UserNameTxt);
        SeePlannedMeals = view.findViewById(R.id.PlannedMealsCard);

        SeePlannedMeals.setOnClickListener(view12 -> {
            fragmentManager.beginTransaction().replace(R.id.flFragment, new ViewScheduleMealPlansFragment()).commit();
            PantryManagerActivity.change();
        });
        ProfileImage.setOnClickListener(view1 -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(requireActivity(), ProfileImage);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.pop_up_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), StartActivity.class));
                    requireActivity().finish();
                }
                return true;
            });

            popup.show();//showing popup menu
        });

        databaseReference.child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Name = snapshot.child("PersonName").getValue().toString();
                    UserNameTxt.setText("Hello " + Name);
                } else
                    UserNameTxt.setText("Hello");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}