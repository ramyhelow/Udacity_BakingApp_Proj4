package com.ramyhelow.udacity_bakingappproj4.util;

import com.ramyhelow.udacity_bakingappproj4.model.Result;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;


public interface MyApiInterface {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Observable<List<Result>> getDetails();

}
