package com.covoitgo.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "signalement")
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSignalement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_auteur")
    private Utilisateur auteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cible")
    private Utilisateur cible;

    private String motif;

    @Enumerated(EnumType.STRING)
    private StatutSignalement statut = StatutSignalement.EN_ATTENTE;

    private String decision;
    private String dateSignalement;

    public Long getIdSignalement() { return idSignalement; }
    public void setIdSignalement(Long idSignalement) { this.idSignalement = idSignalement; }
    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }
    public Utilisateur getCible() { return cible; }
    public void setCible(Utilisateur cible) { this.cible = cible; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public StatutSignalement getStatut() { return statut; }
    public void setStatut(StatutSignalement statut) { this.statut = statut; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getDateSignalement() { return dateSignalement; }
    public void setDateSignalement(String dateSignalement) { this.dateSignalement = dateSignalement; }
}
