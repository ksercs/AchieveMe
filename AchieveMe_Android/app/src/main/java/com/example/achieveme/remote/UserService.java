package com.example.achieveme.remote;

import com.example.achieveme.model.ResObj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserService {

    @GET("/login/")
    Call<ResObj> login(@Header("Username") String username, @Header("Password") String password);
}
