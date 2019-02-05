package com.example.achieveme.remote;

public class ApiUtils {

    public static final String BASE_URL = "http://10.23.74.12:8000/api/";

    public static UserService getUserService () {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
