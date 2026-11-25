package com.covoitgo.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trajet")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTrajet;

    private String villeDepart;
    private String villeArrivee;

    // Format "aaaa-mm-jj hh:mm" (simple a echanger avec Android)
    private String dateDepart;

    // Heure d'arrivee estimee "hh:mm" (calculee a partir de la distance)
    private String heureArrivee;

    // Jours de recurrence, ex. "L,ME,V" (vide = trajet unique)
    private String joursRecurrence;

    private Double distanceKm;
    private Double prixParPlace;
    private Integer nbPlaces;
    private Integer placesRestantes;

    @Enumerated(EnumType.STRING)
    private StatutTrajet statut = StatutTrajet.OUVERT;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_conducteur")
    private Utilisateur conducteur;

    @ManyToOne
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    public Long getIdTrajet() { return idTrajet; }
    public void setIdTrajet(Long idTrajet) { this.idTrajet = idTrajet; }
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    public String getDateDepart() { return dateDepart; }
    public void setDateDepart(String dateDepart) { this.dateDepart = dateDepart; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public Double getPrixParPlace() { return prixParPlace; }
    public void setPrixParPlace(Double prixParPlace) { this.prixParPlace = prixParPlace; }
    public Integer getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(Integer nbPlaces) { this.nbPlaces = nbPlaces; }
    public Integer getPlacesRestantes() { return placesRestantes; }
    public void setPlacesRestantes(Integer placesRestantes) { this.placesRestantes = placesRestantes; }
    public StatutTrajet getStatut() { return statut; }
    public void setStatut(StatutTrajet statut) { this.statut = statut; }
    public String getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(String heureArrivee) { this.heureArrivee = heureArrivee; }
    public String getJoursRecurrence() { return joursRecurrence; }
    public void setJoursRecurrence(String joursRecurrence) { this.joursRecurrence = joursRecurrence; }
    public Vehicule getVehicule() { return vehicule; }
    public void setVehicule(Vehicule vehicule) { this.vehicule = vehicule; }
    public Utilisateur getConducteur() { return conducteur; }
    public void setConducteur(Utilisateur conducteur) { this.conducteur = conducteur; }
}
