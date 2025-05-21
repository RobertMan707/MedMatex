package com.example.medmate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GeminiApiService {

    @POST("v1beta2/models/{model}:generateText")
    Call<GeminiResponse> generateContent(
            @Path("model") String modelName,
            @Body GeminiRequest request,
            @Header("Authorization") String authorization
    );
}
