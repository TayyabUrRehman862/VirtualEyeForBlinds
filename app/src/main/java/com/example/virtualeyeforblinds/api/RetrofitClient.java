package com.example.virtualeyeforblinds.api;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    static Retrofit retrofit;

    WebApi myApi;
    private RetrofitClient() {
        retrofit = new Retrofit.Builder().
                baseUrl(WebApi.BASE_URL)
                .addConverterFactory
                        (GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(WebApi.class);
    }
    public static Retrofit getRetrofit(){
        return retrofit;
    }
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    public WebApi getMyApi() {
        //return null;
        return myApi;
    }
}

