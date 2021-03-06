package com.example.achieveme.remote;

import com.example.achieveme.model.Lists.ListModel;
import com.example.achieveme.model.Lists.ListRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ListsService {

    @GET("/api/{username}/lists/")
    Call<List<ListRes>> userAims(@Path("username") String username, @Header("PASSWORD") String password);

    @DELETE("/api/{username}/{list_id}/")
    Call<ListRes> deleteList(
            @Path("username") String username,
            @Path("list_id") int list_id,
            @Header("PASSWORD") String password);

    @POST("/api/{username}/lists/")
    Call<ListRes> createList(
            @Path("username") String username,
            @Body ListModel list,
            @Header("PASSWORD") String password
    );
}
