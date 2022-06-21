package com.example.lamiacucina;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.lamiacucina.fragments.EditDemandListFragment;
import com.example.lamiacucina.fragments.HomePantryManagerFragment;
import com.example.lamiacucina.fragments.KitchenLogFragment;
import com.example.lamiacucina.fragments.MyFamilyFragment;
import com.example.lamiacucina.fragments.ViewScheduleMealPlansFragment;
import com.google.android.material.navigation.NavigationBarView;

public class PantryManagerActivity  extends BaseActivity implements NavigationBarView.OnItemSelectedListener {
    protected static NavigationBarView navigationBarView;
    protected HomePantryManagerFragment homePantryManagerFragment = new HomePantryManagerFragment(getSupportFragmentManager());
    protected ViewScheduleMealPlansFragment viewScheduleMealPlansFragment = new ViewScheduleMealPlansFragment();
    protected EditDemandListFragment editDemandListFragment = new EditDemandListFragment();
    protected KitchenLogFragment kitchenLogFragment = new KitchenLogFragment();
    protected MyFamilyFragment myFamilyFragment = new MyFamilyFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_manager);
        navigationBarView = findViewById(R.id.bottomNavigationView);
        navigationBarView.setOnItemSelectedListener(this);
        navigationBarView.setSelectedItemId(getNavigationMenuItemId());
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_pantry_manager;
    }

    int getNavigationMenuItemId() {
        return R.id.nav_home;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homePantryManagerFragment).commit();
            //startActivity(new Intent(this, MealPlanner.class));
            return true;
        } else if (itemId == R.id.nav_meal_planner) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, viewScheduleMealPlansFragment).commit();
            //startActivity(new Intent(this, MealPlanner.class));
            return true;
        } else if (itemId == R.id.nav_demand_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, editDemandListFragment).commit();
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

    public static void change()
    {
        navigationBarView.setSelectedItemId(R.id.nav_meal_planner);
    }
}