package com.example.covoitgo.controller;

/** Le Controleur renvoie le resultat a la Vue par cette interface. */
public interface ResultCallback<T> {
    void onSuccess(T resultat);
    void onError(String message);
}
