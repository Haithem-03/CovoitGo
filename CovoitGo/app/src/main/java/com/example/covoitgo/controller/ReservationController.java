package com.example.covoitgo.controller;

import android.content.Context;

import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.Reservation;
import com.example.covoitgo.model.SessionManager;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.api.RetrofitClient;

import java.util.List;

/** UC : Reserver une place, Gerer les reservations, Traiter une reservation. */
public class ReservationController {

    private final SessionManager session;

    public ReservationController(Context context) {
        this.session = new SessionManager(context);
    }

    public void reserver(Trajet trajet, int nbPlaces, ResultCallback<Reservation> callback) {
        if (nbPlaces < 1 || nbPlaces > trajet.getPlacesRestantes()) {
            callback.onError("Choisissez entre 1 et " + trajet.getPlacesRestantes() + " place(s)");
            return;
        }
        Requetes.NouvelleReservation req = new Requetes.NouvelleReservation(
                session.getUtilisateur().getIdUtilisateur(), trajet.getIdTrajet(), nbPlaces);
        RetrofitClient.getApi().reserver(req).enqueue(new ApiCallback<>(callback));
    }

    public void chargerMesReservations(ResultCallback<List<Reservation>> callback) {
        RetrofitClient.getApi().getReservationsPassager(session.getUtilisateur().getIdUtilisateur())
                .enqueue(new ApiCallback<>(callback));
    }

    public void chargerDemandesRecues(ResultCallback<List<Reservation>> callback) {
        RetrofitClient.getApi().getReservationsConducteur(session.getUtilisateur().getIdUtilisateur())
                .enqueue(new ApiCallback<>(callback));
    }

    public void accepter(Reservation r, ResultCallback<Reservation> callback) {
        RetrofitClient.getApi().accepterReservation(r.getIdReservation()).enqueue(new ApiCallback<>(callback));
    }

    public void refuser(Reservation r, ResultCallback<Reservation> callback) {
        RetrofitClient.getApi().refuserReservation(r.getIdReservation()).enqueue(new ApiCallback<>(callback));
    }

    public void annuler(Reservation r, ResultCallback<Reservation> callback) {
        RetrofitClient.getApi().annulerReservation(r.getIdReservation()).enqueue(new ApiCallback<>(callback));
    }
}
