package com.example.achieveme.remote;

public class ApiUtils {

    public static final String BASE_URL = "http://10.23.74.12:8000/";

    public static LoginService getLoginService () {
        return RetrofitClient.getClient(BASE_URL).create(LoginService.class);
    }

    public static ListsService getListsService () {
        return RetrofitClient.getClient(BASE_URL).create(ListsService.class);
    }

    public static AimsListService getAimsService () {
        return RetrofitClient.getClient(BASE_URL).create(AimsListService.class);
    }

    public static AimService getAimService () {
        return RetrofitClient.getClient(BASE_URL).create(AimService.class);
    }
}
