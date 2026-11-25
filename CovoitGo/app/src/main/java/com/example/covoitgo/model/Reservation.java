package com.example.covoitgo.model;

public class Reservation {
    private Long idReservation;
    private Utilisateur passager;
    private Trajet trajet;
    private Integer nbPlaces;
    private String statut;            // EN_ATTENTE, ACCEPTEE, REFUSEE, ANNULEE
    private String dateReservation;

    public Long getIdReservation() { return idReservation; }
    public Utilisateur getPassager() { return passager; }
    public Trajet getTrajet() { return trajet; }
    public Integer getNbPlaces() { return nbPlaces; }
    public String getStatut() { return statut; }
    public String getDateReservation() { return dateReservation; }
}
