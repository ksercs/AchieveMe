package com.example.achieveme.remote;

import com.example.achieveme.model.ResObj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface LoginService {

    @GET("/api/login/")
    Call<ResObj> login (@Header("USERNAME") String username, @Header("PASSWORD") String password);
}
