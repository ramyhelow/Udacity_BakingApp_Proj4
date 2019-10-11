package com.ramyhelow.udacity_bakingappproj4.fragments;


import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VideoFragment extends Fragment implements ExoPlayer.EventListener {


    public static final String TAG = VideoFragment.class.getSimpleName();
    @BindView(R.id.step_description_text_view)
    TextView stepDesc;
    @BindView(R.id.video_view_recipe)
    SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.placeholder_no_video_image)
    ImageView placeHolderImage;
    SimpleExoPlayer simpleExoPlayer;
    long positionPlayer;
    boolean playWhenReady;
    private String description, url, thumbnailImage;
    private boolean pane;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder playbackBuilder;
    private Uri videoUri;

    public VideoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            description = bundle.getString(MyConstants.KEY_STEPS_DESC);
            url = bundle.getString(MyConstants.KEY_STEPS_URL);
            pane = bundle.getBoolean(MyConstants.KEY_PANE_VID);
            thumbnailImage = bundle.getString(MyConstants.THUMBNAIL_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        Log.d(TAG, "In On Create View");
        if (savedInstanceState != null) {
            int placeHolderVisibility = savedInstanceState.getInt(MyConstants.KEY_VISIBILITY_PLACEHOLDER);
            Log.d(TAG, "Visiblity: " + placeHolderVisibility);
            placeHolderImage.setVisibility(placeHolderVisibility);
            int visibilityExo = savedInstanceState.getInt(MyConstants.KEY_VISIBILITY_EXO_PLAYER);
            simpleExoPlayerView.setVisibility(visibilityExo);

            playWhenReady = savedInstanceState.getBoolean(MyConstants.KEY_PLAY_WHEN_READY);
        }
        Log.d(TAG, "URL : " + url);
        if (url != null) {
            if (url.equals("")) {
                Log.d(TAG, "EMPTY URL");
                simpleExoPlayerView.setVisibility(View.GONE);
                placeHolderImage.setVisibility(View.VISIBLE);
                if (!thumbnailImage.equals("")) {

                    Picasso.get().load(thumbnailImage).into(placeHolderImage);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    stepDesc.setText(description);
                } else {
                    hideUI();
                    simpleExoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    simpleExoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            } else {
                if (savedInstanceState != null) {

                    positionPlayer = savedInstanceState.getLong(MyConstants.MEDIA_POS);
                }
                placeHolderImage.setVisibility(View.GONE);
                initializeMedia();
                Log.d(TAG, "URL " + url);
                initializePlayer(Uri.parse(url));
                videoUri = Uri.parse(url);
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    stepDesc.setText(description);
                } else {
                    hideUI();
                    simpleExoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    simpleExoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }
        } else {
            simpleExoPlayerView.setVisibility(View.GONE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                stepDesc.setText(description);
            } else {
                hideUI();
                simpleExoPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                simpleExoPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
        return view;
    }

    private void initializeMedia() {
        mediaSessionCompat = new MediaSessionCompat(getActivity(), TAG);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setMediaButtonReceiver(null);
        playbackBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSessionCompat.setPlaybackState(playbackBuilder.build());
        mediaSessionCompat.setCallback(new SessionCallBacks());
        mediaSessionCompat.setActive(true);
    }

    private void hideUI() {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (simpleExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance
                    (getActivity(), trackSelector, loadControl);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.addListener(this);
            String userAgent = Util.getUserAgent(getActivity(),
                    getActivity().getString(R.string.application_name_exo_player));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getActivity(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MyConstants.KEY_VISIBILITY_EXO_PLAYER, simpleExoPlayerView.getVisibility());
        outState.putInt(MyConstants.KEY_VISIBILITY_PLACEHOLDER, placeHolderImage.getVisibility());
        //Saving current Position before rotation
        outState.putLong(MyConstants.MEDIA_POS, positionPlayer);
        //for preserving state of exoplayer
        outState.putBoolean(MyConstants.KEY_PLAY_WHEN_READY, playWhenReady);
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mediaSessionCompat != null) {
            mediaSessionCompat.setActive(false);
        }
    }

    @Override
    public void onPause() {
        //releasing in Pause and saving current position for resuming
        super.onPause();
        if (simpleExoPlayer != null) {
            positionPlayer = simpleExoPlayer.getCurrentPosition();
            //getting play when ready so that player can be properly store state on rotation
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (simpleExoPlayer != null) {
            //resuming properly
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            simpleExoPlayer.seekTo(positionPlayer);
        } else {
            //Correctly initialize and play properly fromm seekTo function
            initializeMedia();
            initializePlayer(videoUri);
            simpleExoPlayer.setPlayWhenReady(playWhenReady);
            simpleExoPlayer.seekTo(positionPlayer);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            playbackBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    simpleExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            playbackBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    simpleExoPlayer.getCurrentPosition(), 1f);
        }
        mediaSessionCompat.setPlaybackState(playbackBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    private class SessionCallBacks extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            super.onPlay();
            simpleExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            super.onPause();
            simpleExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            simpleExoPlayer.seekTo(0);
        }
    }

}
