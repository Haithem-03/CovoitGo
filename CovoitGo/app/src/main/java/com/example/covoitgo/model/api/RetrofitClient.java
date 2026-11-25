package com.example.covoitgo.model.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Emulateur Android : 10.0.2.2 = le "localhost" de votre PC.
    // Telephone reel : remplacez par l'adresse IP du PC (ex. "http://192.168.1.15:8080/api/"),
    // le telephone et le PC doivent etre sur le meme Wi-Fi.
    public static final String BASE_URL = "http://10.0.2.2:8081/api/";

    private static ApiService api;

    public static ApiService getApi() {
        if (api == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
            api = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return api;
    }
}
