package com.example.achieveme.remote;

import com.example.achieveme.model.Lists.ListRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ListsService {

    @GET("/api/{username}/aims_list/")
    Call<List<ListRes>> userAims(@Path("username") String username, @Header("PASSWORD") String password);
}
