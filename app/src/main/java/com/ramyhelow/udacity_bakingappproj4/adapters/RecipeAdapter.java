package com.ramyhelow.udacity_bakingappproj4.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.activities.DetailActivity;
import com.ramyhelow.udacity_bakingappproj4.model.Ingredient;
import com.ramyhelow.udacity_bakingappproj4.model.Result;
import com.ramyhelow.udacity_bakingappproj4.model.Step;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Result> resultList;
    private String servings;
    private List<Ingredient> ingredientList;
    private List<Step> stepList;
    private Intent intent;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    public RecipeAdapter(Context context, List<Result> resultList) {
        this.context = context;
        this.resultList = resultList;
        sharedPreferences = context.getSharedPreferences(MyConstants.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, final int position) {
        holder.dishText.setText(resultList.get(position).getName());
        servings = context.getString(R.string.servings) + " " +
                String.valueOf(resultList.get(position).getServings());
        String imageUrl = resultList.get(position).getImage();
        if (!imageUrl.equals("")) {
            Picasso.get().load(imageUrl).into(holder.imageView);
        }
        holder.servingText.setText(servings);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList = resultList.get(position).getIngredients();
                stepList = resultList.get(position).getSteps();
                intent = new Intent(context, DetailActivity.class);
                gson = new Gson();
                String ingredientJson = gson.toJson(ingredientList);
                String stepJson = gson.toJson(stepList);
                intent.putExtra(MyConstants.KEY_INGREDIENTS, ingredientJson);
                intent.putExtra(MyConstants.KEY_STEPS, stepJson);
                String resultJson = gson.toJson(resultList.get(position));
                sharedPreferences.edit().putString(MyConstants.WIDGET_RESULT, resultJson).apply();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dish_text_view)
        TextView dishText;

        @BindView(R.id.servings_text_view)
        TextView servingText;

        @BindView(R.id.card_view)
        CardView cardView;

        @BindView(R.id.dish_image_view)
        AppCompatImageView imageView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
