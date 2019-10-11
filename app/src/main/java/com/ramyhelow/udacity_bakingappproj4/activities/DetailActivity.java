package com.ramyhelow.udacity_bakingappproj4.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ramyhelow.udacity_bakingappproj4.fragments.DetailFragment;
import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.fragments.VideoFragment;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    String stepJson, ingredientJson;
    boolean rotationDetails;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState != null) {
            rotationDetails = savedInstanceState.getBoolean(MyConstants.KEY_ROTATION_DETAIL_ACTIVITY);
        }
        if (findViewById(R.id.video_container_tab) != null) {
            twoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_container_tab, new VideoFragment()).commit();
        }
        if (savedInstanceState == null) {
            stepJson = getIntent().getStringExtra(MyConstants.KEY_STEPS);
            ingredientJson = getIntent().getStringExtra(MyConstants.KEY_INGREDIENTS);
            Bundle bundle = new Bundle();
            bundle.putString(MyConstants.KEY_STEPS_JSON, stepJson);
            bundle.putString(MyConstants.KEY_INGREDIENTS_JSON, ingredientJson);
            bundle.putBoolean(MyConstants.KEY_PANE, twoPane);
            Log.d(TAG, "Pane: " + twoPane);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, detailFragment).commit();
            rotationDetails = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.getBoolean(MyConstants.KEY_ROTATION_DETAIL_ACTIVITY, rotationDetails);
    }

}
