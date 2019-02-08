package com.example.achieveme.remote;


import com.example.achieveme.model.Aims.Aim;
import com.example.achieveme.model.Aims.AimRes;
import com.example.achieveme.model.Aims.SubAimRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AimService {

    String path = "/api/{username}/{list_id}/{aim_id}/";

    @GET(path)
    Call<AimRes> aimInfo(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Path("aim_id") int aim_id,
            @Header("PASSWORD") String password);

    @POST(path)
    Call<SubAimRes> editAim(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Path("aim_id") int aim_id,
            @Header("PASSWORD") String password,
            @Body Aim aim);

    @POST(path)
    Call<Aim> markAim(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Path("aim_id") int aim_id,
            @Header("PASSWORD") String password);

    @DELETE(path)
    Call<Aim> deleteAim(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Path("aim_id") int aim_id,
            @Header("PASSWORD") String password);

    @POST("/api/{username}/{list_id}/")
    Call<SubAimRes> newAim(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Body Aim aim,
            @Header("PASSWORD") String password
    );

}
