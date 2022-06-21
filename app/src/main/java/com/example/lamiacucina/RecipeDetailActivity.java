package com.example.lamiacucina;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lamiacucina.model.Recipe;
import com.squareup.picasso.Picasso;

public class RecipeDetailActivity extends AppCompatActivity {
    ImageView RecipeImage;
    TextView ItemCount;
    TextView IngredientsTextView;
    TextView RecipeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("Recipe");

        if (recipe==null)
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            return;
        }

        RecipeImage = findViewById(R.id.RecipeImage);
        ItemCount = findViewById(R.id.ItemCount);
        RecipeTitle = findViewById(R.id.RecipeTitle);
        IngredientsTextView = findViewById(R.id.IngredientsTextView);

        ItemCount.setText(recipe.getIngredients().size() + " Items");
        RecipeTitle.setText(recipe.getTitle());

        if (recipe.getImage() != null)
            Picasso.get().load(recipe.getImage()).into(RecipeImage);
        else
            RecipeImage.setImageResource(R.drawable.oil);

        StringBuilder Ingredient = new StringBuilder();

        for (String ingredient : recipe.getIngredients()) {
            Ingredient.append(ingredient).append("\n\n");
        }

        IngredientsTextView.setText(Ingredient.toString());
    }
}