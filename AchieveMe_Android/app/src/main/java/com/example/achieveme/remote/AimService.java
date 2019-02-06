package com.example.achieveme.remote;


import com.example.achieveme.model.Aims.AimRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AimService {

    @GET("/api/{username}/{list_id}/{aim_id}/")
    Call<AimRes> aimInfo(@Path("username") String username,
                           @Path("list_id") int list_id,
                           @Path("aim_id") int aim_id,
                           @Header("PASSWORD") String password);
}
