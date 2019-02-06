package com.example.achieveme.remote;

import com.example.achieveme.model.Aims.AimRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AimsService {

    @GET("/api/{username}/{list_id}/")
    Call<List<AimRes>> userAims(@Path("username") String username, @Path("list_id") int list_id,
                                @Header("PASSWORD") String password);
}
