package com.example.covoitgo.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/** Garde l'utilisateur connecte en memoire du telephone (SharedPreferences). */
public class SessionManager {

    private static final String PREFS = "covoitgo_session";
    private static final String CLE_UTILISATEUR = "utilisateur";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void enregistrer(Utilisateur utilisateur) {
        prefs.edit().putString(CLE_UTILISATEUR, gson.toJson(utilisateur)).apply();
    }

    public Utilisateur getUtilisateur() {
        String json = prefs.getString(CLE_UTILISATEUR, null);
        return json == null ? null : gson.fromJson(json, Utilisateur.class);
    }

    public boolean estConnecte() {
        return getUtilisateur() != null;
    }

    public void deconnecter() {
        prefs.edit().clear().apply();
    }
}
