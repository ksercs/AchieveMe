package com.example.achieveme.remote;

import com.example.achieveme.model.ResObj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface LoginService {

    @GET("/api/{username}/check_password/")
    Call<ResObj> login (@Path("username") String username, @Header("PASSWORD") String password);
}
