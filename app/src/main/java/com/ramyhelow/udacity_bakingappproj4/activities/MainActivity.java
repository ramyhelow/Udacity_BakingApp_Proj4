package com.ramyhelow.udacity_bakingappproj4.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.util.MyIdlingResource;

public class MainActivity extends AppCompatActivity {

    private static boolean mTwoPane;

    @Nullable
    private MyIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public androidx.test.espresso.IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new MyIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.tab_list_recipe_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        getIdlingResource();
    }



    public static boolean getNoPane() {
        return mTwoPane;
    }
}
