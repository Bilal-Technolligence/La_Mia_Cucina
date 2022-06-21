package com.example.lamiacucina.model;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    String ID;
    String Title;
    String Image;
    String Instruction;
    ArrayList<String> Ingredients = new ArrayList<>();
    boolean Selected;

    public Recipe() {
    }

    public Recipe(String ID, String title, String image, String instruction, ArrayList<String> ingredients) {
        this.ID = ID;
        Title = title;
        Image = image;
        Instruction = instruction;
        Ingredients = ingredients;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getInstruction() {
        return Instruction;
    }

    public void setInstruction(String instruction) {
        Instruction = instruction;
    }

    public ArrayList<String> getIngredients() {
        return Ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        Ingredients = ingredients;
    }

    public void addIngredient(String ingredient)
    {
        Ingredients.add(ingredient);
    }
}
