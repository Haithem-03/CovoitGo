package com.example.covoitgo.model;

public class Reservation {
    private Long idReservation;
    private Utilisateur passager;
    private Trajet trajet;
    private Integer nbPlaces;
    private String statut;            // EN_ATTENTE, ACCEPTEE, REFUSEE, ANNULEE
    private String dateReservation;

    public Reservation() {
    }

    public Reservation(Long idReservation, Utilisateur passager, Trajet trajet,
                       Integer nbPlaces, String statut, String dateReservation) {
        this.idReservation = idReservation;
        this.passager = passager;
        this.trajet = trajet;
        this.nbPlaces = nbPlaces;
        this.statut = statut;
        this.dateReservation = dateReservation;
    }

    public Long getIdReservation() { return idReservation; }
    public void setIdReservation(Long idReservation) { this.idReservation = idReservation; }

    public Utilisateur getPassager() { return passager; }
    public void setPassager(Utilisateur passager) { this.passager = passager; }

    public Trajet getTrajet() { return trajet; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }

    public Integer getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(Integer nbPlaces) { this.nbPlaces = nbPlaces; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDateReservation() { return dateReservation; }
    public void setDateReservation(String dateReservation) { this.dateReservation = dateReservation; }
}