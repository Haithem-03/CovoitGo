package com.example.covoitgo.model;

public class Utilisateur {
    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String telephone;
    private String role;               // PASSAGER, CONDUCTEUR, ADMIN
    private String statutConducteur;   // NON_APPLICABLE, EN_ATTENTE, VALIDE, REFUSE
    private Double noteMoyenne;
    private Integer nbEvaluations;

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String motDePasse, String telephone, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.role = role;
    }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getRole() { return role; }
    public String getStatutConducteur() { return statutConducteur; }
    public Double getNoteMoyenne() { return noteMoyenne == null ? 0.0 : noteMoyenne; }
    public Integer getNbEvaluations() { return nbEvaluations == null ? 0 : nbEvaluations; }

    public String getNomComplet() { return prenom + " " + nom; }

    /** "Sami B." comme sur les maquettes */
    public String getNomCourt() {
        return prenom + (nom != null && !nom.isEmpty() ? " " + nom.substring(0, 1).toUpperCase() + "." : "");
    }

    /** "SB" pour l'avatar */
    public String getInitiales() {
        String p = prenom == null || prenom.isEmpty() ? "" : prenom.substring(0, 1);
        String n = nom == null || nom.isEmpty() ? "" : nom.substring(0, 1);
        return (p + n).toUpperCase();
    }

    /** Conducteur valide par l'administration ("Verifie Esprit") */
    public boolean estVerifie() { return "VALIDE".equals(statutConducteur); }
    public boolean estConducteur() { return "CONDUCTEUR".equals(role); }
    public boolean estAdmin() { return "ADMIN".equals(role); }
}
