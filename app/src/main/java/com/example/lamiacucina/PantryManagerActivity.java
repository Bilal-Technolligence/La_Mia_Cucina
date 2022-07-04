package com.example.lamiacucina;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.example.lamiacucina.fragments.EditDemandListFragment;
import com.example.lamiacucina.fragments.HomePantryManagerFragment;
import com.example.lamiacucina.fragments.KitchenLogFragment;
import com.example.lamiacucina.fragments.MyFamilyFragment;
import com.example.lamiacucina.fragments.ViewScheduleMealPlansFragment;
import com.google.android.material.navigation.NavigationBarView;

public class PantryManagerActivity  extends BaseActivity implements NavigationBarView.OnItemSelectedListener {
    protected static NavigationBarView navigationBarView;
    protected HomePantryManagerFragment homePantryManagerFragment = new HomePantryManagerFragment(getSupportFragmentManager(),this);
    protected ViewScheduleMealPlansFragment viewScheduleMealPlansFragment = new ViewScheduleMealPlansFragment(this);
    protected EditDemandListFragment editDemandListFragment = new EditDemandListFragment(this);
    protected KitchenLogFragment kitchenLogFragment = new KitchenLogFragment(this);
    protected MyFamilyFragment myFamilyFragment = new MyFamilyFragment(this);

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
            return true;
        } else if (itemId == R.id.nav_meal_planner) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, viewScheduleMealPlansFragment).commit();
            return true;
        } else if (itemId == R.id.nav_demand_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, editDemandListFragment).commit();
            return true;
        } else if (itemId == R.id.nav_kitchen_log) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, kitchenLogFragment).commit();
            return true;
        } else if (itemId == R.id.nav_family) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, myFamilyFragment).commit();
            return true;
        }
        return false;
    }

    public static void change()
    {
        navigationBarView.setSelectedItemId(R.id.nav_meal_planner);
    }
}