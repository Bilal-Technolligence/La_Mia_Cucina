package com.example.lamiacucina;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.lamiacucina.fragments.DemandListFragment;
import com.example.lamiacucina.fragments.HomeChefFragment;
import com.example.lamiacucina.fragments.KitchenLogFragment;
import com.example.lamiacucina.fragments.MealPlannerFragment;
import com.example.lamiacucina.fragments.MyFamilyFragment;
import com.google.android.material.navigation.NavigationBarView;

public class ChefActivity extends BaseActivity implements NavigationBarView.OnItemSelectedListener {
    protected NavigationBarView navigationBarView;
    protected HomeChefFragment homeChefFragment = new HomeChefFragment();
    protected MealPlannerFragment mealPlannerFragment = new MealPlannerFragment();
    protected DemandListFragment demandListFragment = new DemandListFragment();
    protected KitchenLogFragment kitchenLogFragment = new KitchenLogFragment();
    protected MyFamilyFragment myFamilyFragment = new MyFamilyFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);

        navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(this);
        navigationBarView.setSelectedItemId(getNavigationMenuItemId());
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_chef;
    }

    int getNavigationMenuItemId() {
        return R.id.nav_home;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeChefFragment).commit();
            //startActivity(new Intent(this, MealPlanner.class));
            return true;
        } else if (itemId == R.id.nav_meal_planner) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, mealPlannerFragment).commit();
            //startActivity(new Intent(this, MealPlanner.class));
            return true;
        } else if (itemId == R.id.nav_demand_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, demandListFragment).commit();
            //startActivity(new Intent(this, DemandListActivity.class));
            return true;
        } else if (itemId == R.id.nav_kitchen_log) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, kitchenLogFragment).commit();
            //startActivity(new Intent(this, KitchenLog.class));
            return true;
        } else if (itemId == R.id.nav_family) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, myFamilyFragment).commit();
            //startActivity(new Intent(this, FamilyActivity.class));
            return true;
        }
        return false;
    }
}