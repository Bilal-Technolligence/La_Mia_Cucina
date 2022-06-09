package com.example.lamiacucina.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lamiacucina.R;
import com.example.lamiacucina.model.Ingredient;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class IngredientListAdaptor extends RecyclerView.Adapter<IngredientListAdaptor.MyHolder> {
    Context ct;
    ArrayList<Ingredient> al;

    public IngredientListAdaptor(Context cont, ArrayList<Ingredient> al) {
        this.ct = cont;
        this.al = al;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Toast.makeText(ct , "Inside Adapter" , Toast.LENGTH_SHORT).show();
        LayoutInflater li = LayoutInflater.from(ct);
        View v = li.inflate(R.layout.ingredient_recyclerview_item, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(IngredientListAdaptor.MyHolder holder, final int position) {
        final Ingredient p1 = al.get(position);

        if (p1.getIngredientName()!=null && !p1.getIngredientName().equals(""))
            holder.IngredientName.setText("Name : " + p1.getIngredientName() + " | " + p1.getIngredientQuantity() + p1.getIngredientUnit());
        else
            holder.IngredientName.setVisibility(View.GONE);

        //holder.cld.setOnClickListener(view -> ct.startActivity(new Intent(ct, DoctorDetail.class).putExtra("doctor", p1)));
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView IngredientName;
        CardView cld;

        public MyHolder(View itemView) {
            super(itemView);
            cld = itemView.findViewById(R.id.IngredientDetailCard);
            image = itemView.findViewById(R.id.image);
            IngredientName = itemView.findViewById(R.id.IngredientName);
        }
    }
}