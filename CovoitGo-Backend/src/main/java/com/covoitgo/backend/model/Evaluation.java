package com.covoitgo.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_auteur")
    private Utilisateur auteur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cible")
    private Utilisateur cible;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_trajet")
    private Trajet trajet;

    private Integer note;
    private String commentaire;

    public Long getIdEvaluation() { return idEvaluation; }
    public void setIdEvaluation(Long idEvaluation) { this.idEvaluation = idEvaluation; }
    public Utilisateur getAuteur() { return auteur; }
    public void setAuteur(Utilisateur auteur) { this.auteur = auteur; }
    public Utilisateur getCible() { return cible; }
    public void setCible(Utilisateur cible) { this.cible = cible; }
    public Trajet getTrajet() { return trajet; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }
    public Integer getNote() { return note; }
    public void setNote(Integer note) { this.note = note; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
