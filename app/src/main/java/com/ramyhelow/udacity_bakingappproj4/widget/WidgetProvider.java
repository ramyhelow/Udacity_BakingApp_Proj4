package com.ramyhelow.udacity_bakingappproj4.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.model.Ingredient;
import com.ramyhelow.udacity_bakingappproj4.model.Result;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;

import java.util.List;


public class WidgetProvider extends AppWidgetProvider {

    SharedPreferences sharedPreferences;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String recipeName, List<Ingredient> ingredientList) {


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.recipe_name_text_view, recipeName);
        views.removeAllViews(R.id.widget_ingredients_container);
        for (Ingredient ingredient : ingredientList) {
            RemoteViews ingredientView = new RemoteViews(context.getPackageName(),
                    R.layout.recipe_list_item);
            ingredientView.setTextViewText(R.id.ingredient_name_text_view,
                    String.valueOf(ingredient.getIngredient()) + " " +
                            String.valueOf(ingredient.getMeasure()));
            views.addView(R.id.widget_ingredients_container, ingredientView);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        sharedPreferences = context.getSharedPreferences(MyConstants.SHARED_PREFERENCES,
                Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(MyConstants.WIDGET_RESULT, null);
        Gson gson = new Gson();
        Result recipe = gson.fromJson(result, Result.class);
        String recipeName = recipe.getName();
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName, recipe.getIngredients());
        }
    }


    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {

    }
}

