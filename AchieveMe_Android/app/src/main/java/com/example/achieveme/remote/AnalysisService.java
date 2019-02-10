package com.example.achieveme.remote;

import com.example.achieveme.model.Aims.SubAimRes;
import com.example.achieveme.model.Analysis.SubAimAnalysis;
import com.example.achieveme.model.Analysis.AimAnalysis;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AnalysisService {
    @POST("/api/{username}/{list_id}/analysis/")
    Call<SubAimRes> voiceSubAim(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Body SubAimAnalysis subaim,
            @Header("PASSWORD") String password
    );

    @POST("/api/{username}/{list_id}/analysis/")
    Call<SubAimRes> voiceAim(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Body AimAnalysis aim,
            @Header("PASSWORD") String password
    );
}
