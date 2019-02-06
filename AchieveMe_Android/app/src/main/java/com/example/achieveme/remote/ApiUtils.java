package com.example.achieveme.remote;

public class ApiUtils {

    private static final String BASE_URL = "http://10.23.74.12:8000/";

    public static LoginService getLoginService () {
        return RetrofitClient.getClient(BASE_URL).create(LoginService.class);
    }

    public static ListsService getListsService () {
        return RetrofitClient.getClient(BASE_URL).create(ListsService.class);
    }

    public static AimsService getAimsService () {
        return RetrofitClient.getClient(BASE_URL).create(AimsService.class);
    }
}
