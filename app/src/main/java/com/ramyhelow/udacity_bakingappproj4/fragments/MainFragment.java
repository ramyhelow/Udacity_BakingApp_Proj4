package com.ramyhelow.udacity_bakingappproj4.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developers.coolprogressviews.DoubleArcProgress;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ramyhelow.udacity_bakingappproj4.R;
import com.ramyhelow.udacity_bakingappproj4.activities.MainActivity;
import com.ramyhelow.udacity_bakingappproj4.adapters.RecipeAdapter;
import com.ramyhelow.udacity_bakingappproj4.model.Result;
import com.ramyhelow.udacity_bakingappproj4.util.MyApiInterface;
import com.ramyhelow.udacity_bakingappproj4.util.MyConstants;
import com.ramyhelow.udacity_bakingappproj4.util.MyIdlingResource;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainFragment extends Fragment {


    private static final String TAG = MainFragment.class.getSimpleName();
    @BindView(R.id.recipe_recycler_view)
    RecyclerView recipeRecyclerView;
    String resultJson;
    Gson gson;
    MyIdlingResource idlingResource;
    @BindView(R.id.double_progress_arc)
    DoubleArcProgress doubleArcProgress;
    private MyApiInterface apiInterface;
    private List<Result> resultList;
    private boolean mTwoPane;
    private RecipeAdapter recipeAdapter;


    public MainFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        apiInterface = MyConstants.getRetrofit().create(MyApiInterface.class);
        idlingResource = (MyIdlingResource) ((MainActivity) getActivity()).getIdlingResource();
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }
        doubleArcProgress.setVisibility(View.VISIBLE);
        if (isNetworkConnected()) {
            resultList = getRecipeList();
        } else {
            Snackbar.make(view, getActivity().getString(R.string.network_error), Snackbar.LENGTH_LONG).show();
        }
        gson = new Gson();
        return view;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public List<Result> getRecipeList() {
        apiInterface.getDetails()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Result>>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Result> value) {
                        resultList = value;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage() + " ");
                        showError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        if (!disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        resultJson = gson.toJson(resultList);
                        recipeAdapter = new RecipeAdapter(getActivity(), resultList);
                        mTwoPane = MainActivity.getNoPane();
                        if (mTwoPane) {
                            //GridLayout
                            GridLayoutManager gridLayoutManager = new
                                    GridLayoutManager(getActivity(), 3);
                            recipeRecyclerView.setLayoutManager(gridLayoutManager);
                            recipeRecyclerView.setAdapter(recipeAdapter);
                            idlingResource.setIdleState(true);
                        } else {
                            LinearLayoutManager linearLayoutManager = new
                                    LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                            recipeRecyclerView.setLayoutManager(linearLayoutManager);
                            recipeRecyclerView.setAdapter(recipeAdapter);
                            idlingResource.setIdleState(true);
                        }
                        doubleArcProgress.setVisibility(View.GONE);
                    }
                });
        return resultList;
    }


    private void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

}
