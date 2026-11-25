package com.covoitgo.backend.service;

import com.covoitgo.backend.dto.LoginRequest;
import com.covoitgo.backend.exception.ApiException;
import com.covoitgo.backend.model.Role;
import com.covoitgo.backend.model.StatutConducteur;
import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.repository.UtilisateurRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurService {

    public static final String DOMAINE_ESPRIT = "@esprit.tn";

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder encoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder encoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.encoder = encoder;
    }

    // UC : S'inscrire
    public Utilisateur inscrire(Utilisateur u) {
        if (vide(u.getNom()) || vide(u.getPrenom()) || vide(u.getEmail()) || vide(u.getMotDePasse())) {
            throw ApiException.invalide("Tous les champs obligatoires doivent etre remplis");
        }
        if (u.getMotDePasse().length() < 6) {
            throw ApiException.invalide("Le mot de passe doit contenir au moins 6 caracteres");
        }
        String email = u.getEmail().trim().toLowerCase();
        if (!email.endsWith(DOMAINE_ESPRIT)) {
            throw ApiException.invalide("Utilisez votre e-mail de l'etablissement (prenom.nom" + DOMAINE_ESPRIT + ")");
        }
        if (utilisateurRepository.existsByEmail(email)) {
            throw ApiException.conflit("Cet email est deja utilise");
        }
        // Tout etudiant commence passager ; il devient conducteur en enregistrant un vehicule
        u.setRole(Role.PASSAGER);
        u.setIdUtilisateur(null);
        u.setEmail(email);
        u.setMotDePasse(encoder.encode(u.getMotDePasse()));
        u.setNoteMoyenne(0.0);
        u.setNbEvaluations(0);
        u.setStatutConducteur(StatutConducteur.NON_APPLICABLE);
        return utilisateurRepository.save(u);
    }

    // UC : S'authentifier
    public Utilisateur login(LoginRequest req) {
        if (req == null || vide(req.email()) || vide(req.motDePasse())) {
            throw ApiException.invalide("Email et mot de passe obligatoires");
        }
        Utilisateur u = utilisateurRepository.findByEmail(req.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Email ou mot de passe incorrect"));
        if (!encoder.matches(req.motDePasse(), u.getMotDePasse())) {
            throw new ApiException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }
        return u;
    }

    public Utilisateur trouver(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> ApiException.introuvable("Utilisateur introuvable"));
    }

    // UC : Gerer profil
    public Utilisateur modifierProfil(Long id, Utilisateur donnees) {
        Utilisateur u = trouver(id);
        if (!vide(donnees.getNom())) u.setNom(donnees.getNom());
        if (!vide(donnees.getPrenom())) u.setPrenom(donnees.getPrenom());
        if (!vide(donnees.getTelephone())) u.setTelephone(donnees.getTelephone());
        if (!vide(donnees.getMotDePasse())) {
            if (donnees.getMotDePasse().length() < 6) {
                throw ApiException.invalide("Le mot de passe doit contenir au moins 6 caracteres");
            }
            u.setMotDePasse(encoder.encode(donnees.getMotDePasse()));
        }
        return utilisateurRepository.save(u);
    }

    // UC : Valider un conducteur (admin)
    public List<Utilisateur> conducteursEnAttente() {
        return utilisateurRepository.findByRoleAndStatutConducteur(Role.CONDUCTEUR, StatutConducteur.EN_ATTENTE);
    }

    public Utilisateur changerStatutConducteur(Long id, StatutConducteur statut) {
        Utilisateur u = trouver(id);
        if (u.getRole() != Role.CONDUCTEUR) {
            throw ApiException.invalide("Cet utilisateur n'est pas conducteur");
        }
        u.setStatutConducteur(statut);
        return utilisateurRepository.save(u);
    }

    /** Appele a l'enregistrement du premier vehicule : l'etudiant devient conducteur en attente. */
    public Utilisateur devenirConducteur(Utilisateur u) {
        u.setRole(Role.CONDUCTEUR);
        if (u.getStatutConducteur() != StatutConducteur.VALIDE) {
            u.setStatutConducteur(StatutConducteur.EN_ATTENTE);
        }
        return utilisateurRepository.save(u);
    }

    public void ajouterNote(Utilisateur cible, int note) {
        int nb = cible.getNbEvaluations() == null ? 0 : cible.getNbEvaluations();
        double moy = cible.getNoteMoyenne() == null ? 0 : cible.getNoteMoyenne();
        double nouvelle = (moy * nb + note) / (nb + 1);
        cible.setNoteMoyenne(Math.round(nouvelle * 10) / 10.0);
        cible.setNbEvaluations(nb + 1);
        utilisateurRepository.save(cible);
    }

    private boolean vide(String s) {
        return s == null || s.trim().isEmpty();
    }
}
