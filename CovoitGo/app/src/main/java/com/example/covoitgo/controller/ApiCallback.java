package com.example.covoitgo.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Transforme une reponse Retrofit en succes / message d'erreur lisible. */
class ApiCallback<T> implements Callback<T> {

    private final ResultCallback<T> callback;

    ApiCallback(ResultCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            callback.onSuccess(response.body());
            return;
        }
        String message = "Erreur " + response.code();
        try {
            if (response.errorBody() != null) {
                JsonObject json = JsonParser.parseString(response.errorBody().string()).getAsJsonObject();
                if (json.has("message")) {
                    message = json.get("message").getAsString();
                }
            }
        } catch (Exception ignored) {
            // corps d'erreur illisible : on garde le code HTTP
        }
        callback.onError(message);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        callback.onError("Serveur injoignable. Verifiez que le backend est lance.");
    }
}
