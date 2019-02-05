package com.example.achieveme.remote;

public class ApiUtils {

    private static final String BASE_URL = "http://192.168.43.208:8000";

    public static LoginService getLoginService () {
        return RetrofitClient.getClient(BASE_URL).create(LoginService.class);
    }

    public static AimsListService getAimsListService () {
        return RetrofitClient.getClient(BASE_URL).create(AimsListService.class);
    }
}
