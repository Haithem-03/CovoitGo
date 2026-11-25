package com.example.covoitgo.controller;

import android.content.Context;

import com.example.covoitgo.model.SessionManager;
import com.example.covoitgo.model.Vehicule;
import com.example.covoitgo.model.api.RetrofitClient;

import java.util.List;

/** UC : Enregistrer son vehicule. */
public class VehiculeController {

    private static final int PLACES_MIN = 1;
    private static final int PLACES_MAX = 8;

    private final SessionManager session;

    public VehiculeController(Context context) {
        this.session = new SessionManager(context);
    }

    public void chargerMesVehicules(ResultCallback<List<Vehicule>> callback) {
        RetrofitClient.getApi().getVehicules(idUtilisateurCourant())
                .enqueue(new ApiCallback<>(callback));
    }

    public void enregistrer(String marque, String modele, String immatriculation, String couleur,
                            String nbPlacesTexte, ResultCallback<Vehicule> callback) {
        if (champsObligatoiresVides(marque, modele, immatriculation, nbPlacesTexte)) {
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

        if (!nbPlacesValide(nbPlaces)) {
            callback.onError("Le nombre de places doit etre entre " + PLACES_MIN + " et " + PLACES_MAX);
            return;
        }

        Vehicule vehicule = new Vehicule(marque, modele, immatriculation, couleur, nbPlaces);
        RetrofitClient.getApi().enregistrerVehicule(idUtilisateurCourant(), vehicule)
                .enqueue(new ApiCallback<>(callback));
    }

    private int idUtilisateurCourant() {
        return session.getUtilisateur().getIdUtilisateur();
    }

    private boolean champsObligatoiresVides(String marque, String modele,
                                            String immatriculation, String nbPlacesTexte) {
        return marque.isEmpty() || modele.isEmpty()
                || immatriculation.isEmpty() || nbPlacesTexte.isEmpty();
    }

    private boolean nbPlacesValide(int nbPlaces) {
        return nbPlaces >= PLACES_MIN && nbPlaces <= PLACES_MAX;
    }
}