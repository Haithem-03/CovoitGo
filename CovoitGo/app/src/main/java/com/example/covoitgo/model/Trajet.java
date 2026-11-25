package com.example.covoitgo.model;

public class Trajet {
    private Long idTrajet;
    private String villeDepart;
    private String villeArrivee;
    private String dateDepart;        // "aaaa-mm-jj hh:mm"
    private String heureArrivee;      // "hh:mm"
    private String joursRecurrence;   // ex. "L,ME,V"
    private Double distanceKm;
    private Double prixParPlace;
    private Integer nbPlaces;
    private Integer placesRestantes;
    private String statut;            // OUVERT, COMPLET, ANNULE
    private Utilisateur conducteur;
    private Vehicule vehicule;

    public Long getIdTrajet() { return idTrajet; }
    public String getVilleDepart() { return villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public String getDateDepart() { return dateDepart; }
    public Double getDistanceKm() { return distanceKm; }
    public Double getPrixParPlace() { return prixParPlace; }
    public Integer getNbPlaces() { return nbPlaces; }
    public Integer getPlacesRestantes() { return placesRestantes; }
    public String getStatut() { return statut; }
    public Utilisateur getConducteur() { return conducteur; }
    public Vehicule getVehicule() { return vehicule; }
    public String getHeureArrivee() { return heureArrivee == null ? "" : heureArrivee; }
    public String getJoursRecurrence() { return joursRecurrence; }

    /** "07:30" */
    public String getHeureDepart() {
        return dateDepart != null && dateDepart.length() >= 16 ? dateDepart.substring(11, 16) : "";
    }

    /** "aaaa-mm-jj" */
    public String getJour() {
        return dateDepart != null && dateDepart.length() >= 10 ? dateDepart.substring(0, 10) : "";
    }

    public String getItineraire() { return villeDepart + " → " + villeArrivee; }
}
