package com.ramyhelow.udacity_bakingappproj4.fragments;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.activities.VideoActivity;
import com.ramyhelow.udacity_bakingappproj4.adapters.VideoAdapter;
import com.ramyhelow.udacity_bakingappproj4.model.Ingredient;
import com.ramyhelow.udacity_bakingappproj4.model.Result;
import com.ramyhelow.udacity_bakingappproj4.model.Step;
import com.ramyhelow.udacity_bakingappproj4.util.MyClickCallBack;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;
import com.ramyhelow.udacity_bakingappproj4.widget.WidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment implements MyClickCallBack {

    private static final String TAG = DetailFragment.class.getSimpleName();
    @BindView(R.id.ingredients_list_text_view)
    TextView ingredientsText;
    @BindView(R.id.step_recycler_view)
    RecyclerView stepRecyclerView;
    String steps, ingredients;
    Gson gson;
    VideoAdapter videoAdapter;
    @BindView(R.id.fab_widget)
    FloatingActionButton widgetAddButton;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    private List<Step> stepList;
    private List<Ingredient> ingredientList;
    private boolean twoPane;
    private Parcelable mListState;

    public DetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        steps = bundle.getString(MyConstants.KEY_STEPS_JSON);
        ingredients = bundle.getString(MyConstants.KEY_INGREDIENTS_JSON);
        gson = new Gson();
        ingredientList = gson.fromJson(ingredients,
                new TypeToken<List<Ingredient>>() {
                }.getType());
        stepList = gson.fromJson(steps,
                new TypeToken<List<Step>>() {
                }.getType());
        twoPane = bundle.getBoolean(MyConstants.KEY_PANE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        StringBuffer stringBuffer = new StringBuffer();
        for (Ingredient ingredient : ingredientList) {
            stringBuffer.append("\u2022 " + ingredient.getQuantity() + " " +
                    ingredient.getIngredient() + " " + ingredient.getMeasure() + "\n");
        }
        setHasOptionsMenu(true);
        ingredientsText.setText(stringBuffer.toString());
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        stepRecyclerView.setLayoutManager(linearLayoutManager);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(MyConstants.RECYCLER_VIEW_STATE);
        }
        Log.d(TAG, stepList.size() + "");
        videoAdapter = new VideoAdapter(getActivity(), stepList);
        videoAdapter.setOnClick(this);
        stepRecyclerView.setAdapter(videoAdapter);
        widgetAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences(MyConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                Result result = gson.fromJson(sharedPreferences.getString(MyConstants.WIDGET_RESULT, null), Result.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                Bundle bundle = new Bundle();
                int appWidgetId = bundle.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                WidgetProvider.updateAppWidget(getActivity(), appWidgetManager, appWidgetId, result.getName(),
                        result.getIngredients());
                Toast.makeText(getActivity(), "Added " + result.getName() + " to Widget.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListState != null) {
            //Restoring recycler view state
            linearLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //storing recycler view state
        outState.putParcelable(MyConstants.RECYCLER_VIEW_STATE, linearLayoutManager.onSaveInstanceState());
    }

    @Override
    public void onClick(Context context, Integer id, String description, String url, String thumbnailUrl) {
        if (twoPane) {
            Bundle bundle = new Bundle();
            bundle.putInt(MyConstants.KEY_STEPS_ID, id);
            bundle.putString(MyConstants.KEY_STEPS_DESC, description);
            bundle.putString(MyConstants.KEY_STEPS_URL, url);
            bundle.putBoolean(MyConstants.KEY_PANE_VID, twoPane);
            bundle.putString(MyConstants.THUMBNAIL_IMAGE, thumbnailUrl);
            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_container_tab, videoFragment).commit();
        } else {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra(MyConstants.KEY_STEPS_ID, id);
            intent.putExtra(MyConstants.KEY_STEPS_DESC, description);
            intent.putExtra(MyConstants.KEY_STEPS_URL, url);
            intent.putExtra(MyConstants.THUMBNAIL_IMAGE, thumbnailUrl);
            context.startActivity(intent);
        }
    }

}
