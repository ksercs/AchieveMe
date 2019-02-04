package com.example.achieveme;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AchieveMeClient {
    @GET("/api/{user}/aims_list")
    Call<List<Model>> userAims(@Path("user") String user);
}
