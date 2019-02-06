package com.example.achieveme.remote;

import com.example.achieveme.model.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AimsListService {

    @GET("/api/{username}/aims_list/")
    Call<List<Model>> userAims(@Path("username") String username, @Header("PASSWORD") String password);
}
