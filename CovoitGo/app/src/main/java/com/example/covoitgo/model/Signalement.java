package com.example.covoitgo.model;

public class Signalement {
    private Long idSignalement;
    private Utilisateur auteur;
    private Utilisateur cible;
    private String motif;
    private String statut;            // EN_ATTENTE, TRAITE
    private String decision;
    private String dateSignalement;

    public Long getIdSignalement() { return idSignalement; }
    public Utilisateur getAuteur() { return auteur; }
    public Utilisateur getCible() { return cible; }
    public String getMotif() { return motif; }
    public String getStatut() { return statut; }
    public String getDecision() { return decision; }
    public String getDateSignalement() { return dateSignalement; }
}
