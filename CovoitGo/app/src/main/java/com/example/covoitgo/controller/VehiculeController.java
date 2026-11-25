package com.example.covoitgo.controller;

import android.content.Context;

import com.example.covoitgo.model.SessionManager;
import com.example.covoitgo.model.Vehicule;
import com.example.covoitgo.model.api.RetrofitClient;

import java.util.List;

/** UC : Enregistrer son vehicule. */
public class VehiculeController {

    private final SessionManager session;

    public VehiculeController(Context context) {
        this.session = new SessionManager(context);
    }

    public void chargerMesVehicules(ResultCallback<List<Vehicule>> callback) {
        RetrofitClient.getApi().getVehicules(session.getUtilisateur().getIdUtilisateur())
                .enqueue(new ApiCallback<>(callback));
    }

    public void enregistrer(String marque, String modele, String immatriculation, String couleur,
                            String nbPlacesTexte, ResultCallback<Vehicule> callback) {
        if (marque.isEmpty() || modele.isEmpty() || immatriculation.isEmpty() || nbPlacesTexte.isEmpty()) {
            callback.onError("Marque, modele, immatriculation et places sont obligatoires");
            return;
        }
        int nbPlaces;
        try {
            nbPlaces = Integer.parseInt(nbPlacesTexte);
        } catch (NumberFormatException e) {
            callback.onError("Nombre de places invalide");
            return;
        }
        if (nbPlaces < 1 || nbPlaces > 8) {
            callback.onError("Le nombre de places doit etre entre 1 et 8");
            return;
        }
        Vehicule v = new Vehicule(marque, modele, immatriculation, couleur, nbPlaces);
        RetrofitClient.getApi().enregistrerVehicule(session.getUtilisateur().getIdUtilisateur(), v)
                .enqueue(new ApiCallback<>(callback));
    }
}
