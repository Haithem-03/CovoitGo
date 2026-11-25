package com.example.covoitgo.controller;

import android.content.Context;

import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.SessionManager;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.api.RetrofitClient;

import java.util.List;

/** UC : Gerer les trajets, Estimer le prix de la place, recherche de trajets. */
public class TrajetController {

    private static final String STATUT_ANNULE = "ANNULE";

    private final SessionManager session;

    public TrajetController(Context context) {
        this.session = new SessionManager(context);
    }

    public void estimerPrix(String depart, String arrivee, ResultCallback<Requetes.PrixEstime> callback) {
        if (depart.isEmpty() || arrivee.isEmpty()) {
            callback.onError("Saisissez le depart et l'arrivee");
            return;
        }
        RetrofitClient.getApi().estimerPrix(depart, arrivee).enqueue(new ApiCallback<>(callback));
    }

    /**
     * @param prixMax prix maximum connu (estimation), ou null si pas encore estime
     * @param jours   jours coches ("L","MA","ME","J","V","S"), ou null si trajet unique
     */
    public void publier(String depart, String arrivee, String date, String heure, int nbPlaces, String prixTexte,
                        Double prixMax, List<String> jours, ResultCallback<Trajet> callback) {
        if (depart.isEmpty() || arrivee.isEmpty()) {
            callback.onError("Saisissez le depart et l'arrivee");
            return;
        }
        if (depart.equalsIgnoreCase(arrivee)) {
            callback.onError("Le depart et l'arrivee doivent etre differents");
            return;
        }
        if (date == null || heure == null) {
            callback.onError("Choisissez la date et l'heure de depart");
            return;
        }
        if (nbPlaces < 1) {
            callback.onError("Proposez au moins une place");
            return;
        }

        Double prix = null;
        if (!prixTexte.isEmpty()) {
            String erreurPrix = validerPrix(prixTexte, prixMax);
            if (erreurPrix != null) {
                callback.onError(erreurPrix);
                return;
            }
            prix = lirePrix(prixTexte);
        }

        if (jours != null && jours.isEmpty()) {
            callback.onError("Cochez au moins un jour pour un trajet recurrent");
            return;
        }

        Requetes.NouveauTrajet req = new Requetes.NouveauTrajet(idUtilisateurCourant(),
                depart, arrivee, date, heure, nbPlaces, prix, jours);
        RetrofitClient.getApi().publierTrajet(req).enqueue(new ApiCallback<>(callback));
    }

    /** @param date "aaaa-mm-jj" */
    public void rechercher(String depart, String arrivee, String date, int places,
                           ResultCallback<List<Trajet>> callback) {
        RetrofitClient.getApi()
                .rechercherTrajets(nullSiVide(depart), nullSiVide(arrivee),
                        date, places, idUtilisateurCourant())
                .enqueue(new ApiCallback<>(callback));
    }

    public void trouver(Long idTrajet, ResultCallback<Trajet> callback) {
        RetrofitClient.getApi().getTrajet(idTrajet).enqueue(new ApiCallback<>(callback));
    }

    public void chargerMesTrajets(ResultCallback<List<Trajet>> callback) {
        RetrofitClient.getApi().getTrajetsConducteur(idUtilisateurCourant())
                .enqueue(new ApiCallback<>(callback));
    }

    public void annuler(Trajet trajet, ResultCallback<Trajet> callback) {
        if (STATUT_ANNULE.equals(trajet.getStatut())) {
            callback.onError("Ce trajet est deja annule");
            return;
        }
        RetrofitClient.getApi().annulerTrajet(trajet.getIdTrajet()).enqueue(new ApiCallback<>(callback));
    }

    // ---- Methodes utilitaires privees ----

    private int idUtilisateurCourant() {
        return session.getUtilisateur().getIdUtilisateur();
    }

    private static String nullSiVide(String texte) {
        return texte.isEmpty() ? null : texte;
    }

    private static Double lirePrix(String prixTexte) {
        return Double.parseDouble(prixTexte.replace(',', '.'));
    }

    /** Retourne le message d'erreur si le prix est invalide, sinon null. */
    private static String validerPrix(String prixTexte, Double prixMax) {
        double prix;
        try {
            prix = lirePrix(prixTexte);
        } catch (NumberFormatException e) {
            return "Prix invalide";
        }
        if (prix <= 0) {
            return "Le prix doit etre superieur a 0";
        }
        if (prixMax != null && prix > prixMax) {
            return "Le prix maximum pour ce trajet est de " + prixMax + " DT";
        }
        return null;
    }
}