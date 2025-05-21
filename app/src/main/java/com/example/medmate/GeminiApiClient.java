package com.example.medmate;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeminiApiClient {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private static GeminiApiService service;

    public static GeminiApiService getService() {
        if (service == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            service = retrofit.create(GeminiApiService.class);
        }
        return service;
    }
}
