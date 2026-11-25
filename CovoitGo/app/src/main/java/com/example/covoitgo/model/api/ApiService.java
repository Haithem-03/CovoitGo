package com.example.covoitgo.model.api;

import com.example.covoitgo.model.Requetes;
import com.example.covoitgo.model.Reservation;
import com.example.covoitgo.model.Signalement;
import com.example.covoitgo.model.Trajet;
import com.example.covoitgo.model.Utilisateur;
import com.example.covoitgo.model.Vehicule;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** Tous les appels vers le backend Spring Boot. */
public interface ApiService {

    // ---- Authentification / profil
    @POST("auth/inscription")
    Call<Utilisateur> inscrire(@Body Utilisateur utilisateur);

    @POST("auth/login")
    Call<Utilisateur> login(@Body Requetes.Login request);

    @GET("utilisateurs/{id}")
    Call<Utilisateur> getUtilisateur(@Path("id") Long id);

    @PUT("utilisateurs/{id}")
    Call<Utilisateur> modifierProfil(@Path("id") Long id, @Body Utilisateur donnees);

    // ---- Vehicule
    @POST("vehicules/conducteur/{id}")
    Call<Vehicule> enregistrerVehicule(@Path("id") Long idConducteur, @Body Vehicule vehicule);

    @GET("vehicules/conducteur/{id}")
    Call<List<Vehicule>> getVehicules(@Path("id") Long idConducteur);

    // ---- Trajets
    @GET("trajets/estimer")
    Call<Requetes.PrixEstime> estimerPrix(@Query("depart") String depart, @Query("arrivee") String arrivee);

    @POST("trajets")
    Call<Trajet> publierTrajet(@Body Requetes.NouveauTrajet request);

    @GET("trajets/recherche")
    Call<List<Trajet>> rechercherTrajets(@Query("depart") String depart, @Query("arrivee") String arrivee,
                                         @Query("date") String date, @Query("places") Integer places,
                                         @Query("exclure") Long idUtilisateurConnecte);

    @GET("trajets/{id}")
    Call<Trajet> getTrajet(@Path("id") Long idTrajet);

    @GET("trajets/conducteur/{id}")
    Call<List<Trajet>> getTrajetsConducteur(@Path("id") Long idConducteur);

    @PUT("trajets/{id}/annuler")
    Call<Trajet> annulerTrajet(@Path("id") Long idTrajet);

    // ---- Reservations
    @POST("reservations")
    Call<Reservation> reserver(@Body Requetes.NouvelleReservation request);

    @GET("reservations/passager/{id}")
    Call<List<Reservation>> getReservationsPassager(@Path("id") Long idPassager);

    @GET("reservations/conducteur/{id}")
    Call<List<Reservation>> getReservationsConducteur(@Path("id") Long idConducteur);

    @PUT("reservations/{id}/accepter")
    Call<Reservation> accepterReservation(@Path("id") Long idReservation);

    @PUT("reservations/{id}/refuser")
    Call<Reservation> refuserReservation(@Path("id") Long idReservation);

    @PUT("reservations/{id}/annuler")
    Call<Reservation> annulerReservation(@Path("id") Long idReservation);

    // ---- Evaluations et signalements
    @POST("evaluations")
    Call<Object> evaluer(@Body Requetes.NouvelleEvaluation request);

    @POST("signalements")
    Call<Object> signaler(@Body Requetes.NouveauSignalement request);

    // ---- Administration
    @GET("admin/conducteurs/en-attente")
    Call<List<Utilisateur>> getConducteursEnAttente();

    @PUT("admin/conducteurs/{id}/valider")
    Call<Utilisateur> validerConducteur(@Path("id") Long id);

    @PUT("admin/conducteurs/{id}/refuser")
    Call<Utilisateur> refuserConducteur(@Path("id") Long id);

    @GET("admin/signalements")
    Call<List<Signalement>> getSignalements();

    @PUT("admin/signalements/{id}/traiter")
    Call<Signalement> traiterSignalement(@Path("id") Long id, @Body Requetes.Decision decision);
}
