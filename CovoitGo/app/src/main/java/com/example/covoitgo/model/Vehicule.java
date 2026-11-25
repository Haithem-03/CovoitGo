package com.example.covoitgo.model;

/** Vehicule d'un conducteur (marque, modele, immatriculation, couleur, nombre de places). */
public class Vehicule {

    // Les noms des champs correspondent au JSON de l'API : ne pas les renommer.
    private Long idVehicule;
    private String marque;
    private String modele;
    private String immatriculation;
    private String couleur;
    private Integer nbPlaces;

    /** Constructeur vide requis par Gson/Retrofit. */
    public Vehicule() {
    }

    public Vehicule(String marque, String modele, String immatriculation, String couleur, Integer nbPlaces) {
        this();
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.nbPlaces = nbPlaces;
    }

    public Long getIdVehicule() {
        return idVehicule;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public String getCouleur() {
        return couleur;
    }

    public Integer getNbPlaces() {
        return nbPlaces;
    }
}