package com.example.covoitgo.model;

public class Vehicule {
    private Long idVehicule;
    private String marque;
    private String modele;
    private String immatriculation;
    private String couleur;
    private Integer nbPlaces;

    public Vehicule() {}

    public Vehicule(String marque, String modele, String immatriculation, String couleur, Integer nbPlaces) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.couleur = couleur;
        this.nbPlaces = nbPlaces;
    }

    public Long getIdVehicule() { return idVehicule; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public String getImmatriculation() { return immatriculation; }
    public String getCouleur() { return couleur; }
    public Integer getNbPlaces() { return nbPlaces; }
}
