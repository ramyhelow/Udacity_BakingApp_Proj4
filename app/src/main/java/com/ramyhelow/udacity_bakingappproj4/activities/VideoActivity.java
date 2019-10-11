package com.ramyhelow.udacity_bakingappproj4.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.fragments.VideoFragment;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;


public class VideoActivity extends AppCompatActivity {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private Bundle bundle;
    private boolean fragmentCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (savedInstanceState != null) {
            fragmentCreated = savedInstanceState.getBoolean(MyConstants.KEY_ROTATION_VIDEO_ACTIVITY);
        }
        if (!fragmentCreated) {
            bundle = new Bundle();
            bundle = getIntent().getExtras();
            VideoFragment videoFragment = new VideoFragment();
            videoFragment.setArguments(bundle);
            fragmentCreated = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.video_fragment, videoFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MyConstants.KEY_ROTATION_VIDEO_ACTIVITY, fragmentCreated);
    }
}
