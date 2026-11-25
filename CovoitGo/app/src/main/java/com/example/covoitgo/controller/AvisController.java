package com.example.covoitgo.controller;

import android.content.Context;

import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.SessionManager;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.api.RetrofitClient;

/** UC : Evaluer, Signaler un comportement. */
public class AvisController {

    private final SessionManager session;

    public AvisController(Context context) {
        this.session = new SessionManager(context);
    }

    public void evaluer(Long idCible, Long idTrajet, int note, String commentaire, ResultCallback<Object> callback) {
        if (note < 1 || note > 5) {
            callback.onError("Choisissez une note entre 1 et 5 etoiles");
            return;
        }
        Requetes.NouvelleEvaluation req = new Requetes.NouvelleEvaluation(
                session.getUtilisateur().getIdUtilisateur(), idCible, idTrajet, note, commentaire);
        RetrofitClient.getApi().evaluer(req).enqueue(new ApiCallback<>(callback));
    }

    public void signaler(Long idCible, String motif, ResultCallback<Object> callback) {
        if (motif.trim().length() < 5) {
            callback.onError("Decrivez le probleme (5 caracteres minimum)");
            return;
        }
        Requetes.NouveauSignalement req = new Requetes.NouveauSignalement(
                session.getUtilisateur().getIdUtilisateur(), idCible, motif.trim());
        RetrofitClient.getApi().signaler(req).enqueue(new ApiCallback<>(callback));
    }

    /** "Signaler ce trajet" (maquette 3) : le signalement vise le conducteur, avec le trajet en contexte. */
    public void signalerTrajet(Trajet trajet, String motif, ResultCallback<Object> callback) {
        if (motif.trim().length() < 5) {
            callback.onError("Decrivez le probleme (5 caracteres minimum)");
            return;
        }
        signaler(trajet.getConducteur().getIdUtilisateur(),
                "Trajet " + trajet.getItineraire() + " du " + trajet.getDateDepart() + " : " + motif.trim(), callback);
    }
}
