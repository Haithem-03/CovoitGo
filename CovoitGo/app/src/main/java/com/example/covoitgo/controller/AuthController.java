package com.example.covoitgo.controller;

import android.content.Context;
import android.util.Patterns;

import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.SessionManager;
import com.example.covoitgo.model.Utilisateur;
import com.example.covoitgo.model.api.RetrofitClient;

/** UC : S'inscrire, S'authentifier, Gerer profil. */
public class AuthController {

    private final SessionManager session;

    public AuthController(Context context) {
        this.session = new SessionManager(context);
    }

    public Utilisateur getUtilisateurConnecte() {
        return session.getUtilisateur();
    }

    public void seConnecter(String email, String motDePasse, ResultCallback<Utilisateur> callback) {
        if (email.isEmpty() || motDePasse.isEmpty()) {
            callback.onError("Veuillez remplir tous les champs");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback.onError("Email invalide");
            return;
        }
        RetrofitClient.getApi().login(new Requetes.Login(email, motDePasse))
                .enqueue(new ApiCallback<>(new ResultCallback<Utilisateur>() {
                    @Override
                    public void onSuccess(Utilisateur u) {
                        session.enregistrer(u);
                        callback.onSuccess(u);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                }));
    }

    public static final String DOMAINE_ESPRIT = "@esprit.tn";

    public void inscrire(String nom, String prenom, String email, String telephone, String motDePasse,
                         String confirmation, ResultCallback<Utilisateur> callback) {
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || motDePasse.isEmpty()) {
            callback.onError("Veuillez remplir tous les champs");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.toLowerCase().endsWith(DOMAINE_ESPRIT)) {
            callback.onError("Utilisez votre e-mail de l'etablissement (prenom.nom" + DOMAINE_ESPRIT + ")");
            return;
        }
        if (!telephone.matches("\\d{8}")) {
            callback.onError("Le telephone doit contenir 8 chiffres");
            return;
        }
        if (motDePasse.length() < 6) {
            callback.onError("Le mot de passe doit contenir au moins 6 caracteres");
            return;
        }
        if (!motDePasse.equals(confirmation)) {
            callback.onError("Les mots de passe ne correspondent pas");
            return;
        }
        Utilisateur u = new Utilisateur(nom, prenom, email, motDePasse, telephone, "PASSAGER");
        RetrofitClient.getApi().inscrire(u).enqueue(new ApiCallback<>(new ResultCallback<Utilisateur>() {
            @Override
            public void onSuccess(Utilisateur cree) {
                session.enregistrer(cree);
                callback.onSuccess(cree);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        }));
    }

    /** Recharge l'utilisateur depuis le serveur (ex. statut conducteur valide par l'admin). */
    public void rafraichir(ResultCallback<Utilisateur> callback) {
        Utilisateur u = session.getUtilisateur();
        if (u == null) {
            callback.onError("Session expiree");
            return;
        }
        RetrofitClient.getApi().getUtilisateur(u.getIdUtilisateur())
                .enqueue(new ApiCallback<>(new ResultCallback<Utilisateur>() {
                    @Override
                    public void onSuccess(Utilisateur maj) {
                        session.enregistrer(maj);
                        callback.onSuccess(maj);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                }));
    }

    public void modifierProfil(String nom, String prenom, String telephone, String nouveauMotDePasse,
                               ResultCallback<Utilisateur> callback) {
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty()) {
            callback.onError("Nom, prenom et telephone sont obligatoires");
            return;
        }
        if (!telephone.matches("\\d{8}")) {
            callback.onError("Le telephone doit contenir 8 chiffres");
            return;
        }
        if (!nouveauMotDePasse.isEmpty() && nouveauMotDePasse.length() < 6) {
            callback.onError("Le mot de passe doit contenir au moins 6 caracteres");
            return;
        }
        Utilisateur actuel = session.getUtilisateur();
        Utilisateur donnees = new Utilisateur();
        donnees.setNom(nom);
        donnees.setPrenom(prenom);
        donnees.setTelephone(telephone);
        if (!nouveauMotDePasse.isEmpty()) {
            donnees.setMotDePasse(nouveauMotDePasse);
        }
        RetrofitClient.getApi().modifierProfil(actuel.getIdUtilisateur(), donnees)
                .enqueue(new ApiCallback<>(new ResultCallback<Utilisateur>() {
                    @Override
                    public void onSuccess(Utilisateur maj) {
                        session.enregistrer(maj);
                        callback.onSuccess(maj);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                }));
    }

    public void seDeconnecter() {
        session.deconnecter();
    }
}
