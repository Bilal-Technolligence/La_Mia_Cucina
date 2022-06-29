package com.example.lamiacucina.fragments;

import android.content.Intent;
import android.graphics.Color;
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

import com.example.lamiacucina.ChefActivity;
import com.example.lamiacucina.PantryManagerActivity;
import com.example.lamiacucina.R;
import com.example.lamiacucina.RecipeDetailActivity;
import com.example.lamiacucina.SelectRecipesActivity;
import com.example.lamiacucina.StartActivity;
import com.example.lamiacucina.activity.recipe.AddRecipeActivity;
import com.example.lamiacucina.adapter.SelectRecipesAdaptor;
import com.example.lamiacucina.adapter.TrendingRecyclerViewAdapter;
import com.example.lamiacucina.model.Ingredient;
import com.example.lamiacucina.model.Recipe;
import com.example.lamiacucina.util.BaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeChefFragment extends Fragment implements TrendingRecyclerViewAdapter.ItemClickListener {
    CircleImageView ProfileImage;
    TextView UserNameTxt;
    TextView RecipesCount;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    TrendingRecyclerViewAdapter trendingRecyclerViewAdapter;
    private SliderView sliderView;
    private final ArrayList<Recipe> currentList = new ArrayList<>();
    private View no_Trending_ads_layout;
    CardView SeeRecipes;
    FragmentManager fragmentManager;

    public HomeChefFragment(FragmentManager supportFragmentManager) {
        // Required empty public constructor
        fragmentManager = supportFragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ProfileImage = view.findViewById(R.id.profile_image);
        UserNameTxt = view.findViewById(R.id.UserNameTxt);
        no_Trending_ads_layout = view.findViewById(R.id.no_Trending_ads_layout);
        sliderView = view.findViewById(R.id.imageSlider);
        RecipesCount = view.findViewById(R.id.RecipesCount);
        SeeRecipes = view.findViewById(R.id.CardView);

        SeeRecipes.setOnClickListener(view12 -> {
            fragmentManager.beginTransaction().replace(R.id.flFragment, new ViewAllRecipesFragment()).commit();
            ChefActivity.change();
        });

        ProfileImage.setOnClickListener(view1 -> {
            //Creating the instance of PopupMenu
            PopupMenu popup = new PopupMenu(requireActivity(), ProfileImage);
            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.pop_up_menu, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.logout) {
                    new BaseUtil(requireActivity()).ClearPreferences();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), StartActivity.class));
                    requireActivity().finish();
                }
                else if (item.getItemId() == R.id.addRecipe)
                {
                    startActivity(new Intent(getActivity(), AddRecipeActivity.class));
                }
                return true;
            });
            popup.show(); //showing popup menu
        });

        databaseReference.child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String Name = snapshot.child("PersonName").getValue(String.class);
                    UserNameTxt.setText(requireActivity().getResources().getString(R.string.hello_with_name,Name));
                } else
                    UserNameTxt.setText(getResources().getString(R.string.hello));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startSlider();

        databaseReference.child("Recipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count = 0;
                    for (DataSnapshot eachAdRecord : dataSnapshot.getChildren()) {
                        if (eachAdRecord.child("FamilyID").exists() && !eachAdRecord.child("FamilyID").getValue(String.class).equals(""))
                        {
                            String mFamilyID = eachAdRecord.child("FamilyID").getValue(String.class);
                            String FamilyID = new BaseUtil(requireActivity()).getFamilyID();

                            if (mFamilyID.equals(FamilyID))
                            {
                                count++;
                            }
                        }
                        if (count != 0)
                        {
                            RecipesCount.setText(getResources().getString(R.string.recipes_count,count));
                        }
                        else
                            RecipesCount.setText(getResources().getString(R.string.recipes_no_count));
                    }

                } else {
                    RecipesCount.setText(getResources().getString(R.string.recipes_no_count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void startSlider() {
        trendingRecyclerViewAdapter = new TrendingRecyclerViewAdapter(getActivity(), this, currentList);
        sliderView.setSliderAdapter(trendingRecyclerViewAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINDEPTHTRANSFORMATION); //set animation for slider
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT); //set rotation type for slider
        sliderView.setIndicatorSelectedColor(Color.WHITE); //set indicator selected color
        sliderView.setIndicatorUnselectedColor(Color.GRAY); //set indicator Unselected color
        sliderView.setScrollTimeInSec(3); //set slide time
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        getTrending();
    }

    private void getTrending() {
        databaseReference.child("Recipes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot eachAdRecord : snapshot.getChildren()) {
                        Recipe recipe = new Recipe();
                        recipe.setID(eachAdRecord.getKey());
                        recipe.setTitle(eachAdRecord.child("title").getValue(String.class));
                        recipe.setImage(eachAdRecord.child("Image").getValue(String.class));
                        recipe.setInstruction(eachAdRecord.child("Instructions").getValue(String.class));

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

                    if (!currentList.isEmpty()) {
                        ShowTrending();
                        trendingRecyclerViewAdapter.renewItems(currentList);
                    } else {
                        HideTrending();
                    }
                } else {
                    HideTrending();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ShowTrending() {
        no_Trending_ads_layout.setVisibility(View.GONE);
        sliderView.setVisibility(View.VISIBLE);
    }

    private void HideTrending() {
        no_Trending_ads_layout.setVisibility(View.VISIBLE);
        sliderView.setVisibility(View.GONE);
    }

    /*private void SaveRecipe() {
        HashMap<String, Object> recipe = new HashMap<>();

        recipe.put("title", "Miso-Butter Roast Chicken With Acorn Squash Panzanella");
        recipe.put("Instructions", "Pat chicken dry with paper towels, season all over with 2 tsp. salt, and tie legs together with kitchen twine. Let sit at room temperature 1 hour. Meanwhile, halve squash and scoop out seeds. Run a vegetable peeler along ridges of squash halves to remove skin. Cut each half into Â½'' -thick wedges; arrange on a rimmed baking sheet. Combine sage, rosemary, and 6 Tbsp. melted butter in a large bowl; pour half of mixture over squash on baking sheet. Sprinkle squash with allspice, red pepper flakes, and Â½ tsp. salt and season with black pepper; toss to coat. Add bread, apples, oil, and Â¼ tsp. salt to remaining herb butter in bowl; season with black pepper and toss to combine. Set aside. Place onion and vinegar in a small bowl; season with salt and toss to coat. Let sit, tossing occasionally, until ready to serve. Place a rack in middle and lower third of oven; preheat to 425Â°F. Mix miso and 3 Tbsp. room-temperature butter in a small bowl until smooth. Pat chicken dry with paper towels, then rub or brush all over with miso butter. Place chicken in a large cast-iron skillet and roast on middle rack until an instant-read thermometer inserted into the thickest part of breast registers 155Â°F, 50â€“60 minutes. (Temperature will climb to 165Â°F while chicken rests.) Let chicken rest in skillet at least 5 minutes, then transfer to a plate; reserve skillet. Meanwhile, roast squash on lower rack until mostly tender, about 25 minutes. Remove from oven and scatter reserved bread mixture over, spreading into as even a layer as you can manage. Return to oven and roast until bread is golden brown and crisp and apples are tender, about 15 minutes. Remove from oven, drain pickled onions, and toss to combine. Transfer to a serving dish. Using your fingers, mash flour and butter in a small bowl to combine. Set reserved skillet with chicken drippings over medium heat. You should have about Â¼ cup, but a little over or under is all good. (If you have significantly more, drain off and set excess aside.) Add wine and cook, stirring often and scraping up any browned bits with a wooden spoon, until bits are loosened and wine is reduced by about half (you should be able to smell the wine), about 2 minutes. Add butter mixture; cook, stirring often, until a smooth paste forms, about 2 minutes. Add broth and any reserved drippings and cook, stirring constantly, until combined and thickened, 6â€“8 minutes. Remove from heat and stir in miso. Taste and season with salt and black pepper. Serve chicken with gravy and squash panzanella alongside.");

        HashMap<String, String> ingredients = new HashMap<>();
        ingredients.put("1", "1 (3½-lb.) whole chicken");
        ingredients.put("2", "2¾ tsp. kosher salt, divided, plus more");
        ingredients.put("3", "2 small acorn squash (about 3 lb. total)");
        ingredients.put("4", "2 Tbsp. finely chopped sage");
        ingredients.put("5", "1 Tbsp. finely chopped rosemary");
        ingredients.put("6", "6 Tbsp. unsalted butter, melted, plus 3 Tbsp. room temperature");
        ingredients.put("7", "¼ tsp. ground allspice");
        ingredients.put("8", "Pinch of crushed red pepper flakes");
        ingredients.put("9", "Freshly ground black pepper");
        ingredients.put("10", "â…“ loaf good-quality sturdy white bread, torn into 1\" pieces (about 2Â½ cups)");
        ingredients.put("11", "Freshly ground black pepper");
        ingredients.put("12", "2 medium apples (such as Gala or Pink Lady; about 14 oz. total), cored, cut into 1\" pieces");
        ingredients.put("13", "2 Tbsp. extra-virgin olive oil");
        ingredients.put("14", "Â½ small red onion, thinly sliced");
        ingredients.put("15", "3 Tbsp. apple cider vinegar");
        ingredients.put("16", "Â¼ cup all-purpose flour");
        ingredients.put("17", "2 Tbsp. unsalted butter, room temperature");
        ingredients.put("18", "Â¼ cup dry white wine");
        ingredients.put("19", "2 cups unsalted chicken broth");
        ingredients.put("20", "2 tsp. white miso");
        ingredients.put("21", "Kosher salt, freshly ground pepper");

        recipe.put("ingredients", ingredients);

        databaseReference.child("Recipes").child("1").setValue(recipe);
    }*/

    @Override
    public void onItemClick(View view, int position) {
        if (position >= 0) {
            Intent o = new Intent(getActivity(), RecipeDetailActivity.class);
            o.putExtra("Recipe", currentList.get(position));
            startActivity(o);
        }
    }
}