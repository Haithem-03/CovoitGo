package com.example.covoitgo.controller;

import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.Signalement;
import com.example.covoitgo.model.Utilisateur;
import com.example.covoitgo.model.api.RetrofitClient;

import java.util.List;

/** UC : Valider un conducteur, Gerer les reclamations. */
public class AdminController {

    public void chargerConducteursEnAttente(ResultCallback<List<Utilisateur>> callback) {
        RetrofitClient.getApi().getConducteursEnAttente().enqueue(new ApiCallback<>(callback));
    }

    public void validerConducteur(Utilisateur u, ResultCallback<Utilisateur> callback) {
        RetrofitClient.getApi().validerConducteur(u.getIdUtilisateur()).enqueue(new ApiCallback<>(callback));
    }

    public void refuserConducteur(Utilisateur u, ResultCallback<Utilisateur> callback) {
        RetrofitClient.getApi().refuserConducteur(u.getIdUtilisateur()).enqueue(new ApiCallback<>(callback));
    }

    public void chargerSignalements(ResultCallback<List<Signalement>> callback) {
        RetrofitClient.getApi().getSignalements().enqueue(new ApiCallback<>(callback));
    }

    public void traiterSignalement(Signalement s, String decision, ResultCallback<Signalement> callback) {
        if (decision.trim().isEmpty()) {
            callback.onError("Saisissez la decision");
            return;
        }
        RetrofitClient.getApi().traiterSignalement(s.getIdSignalement(), new Requetes.Decision(decision.trim()))
                .enqueue(new ApiCallback<>(callback));
    }
}
