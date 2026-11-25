package com.covoitgo.backend.service;

import com.covoitgo.backend.dto.EvaluationRequest;
import com.covoitgo.backend.exception.ApiException;
import com.covoitgo.backend.model.Evaluation;
import com.covoitgo.backend.model.Trajet;
import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UtilisateurService utilisateurService;
    private final TrajetService trajetService;

    public EvaluationService(EvaluationRepository evaluationRepository, UtilisateurService utilisateurService,
                             TrajetService trajetService) {
        this.evaluationRepository = evaluationRepository;
        this.utilisateurService = utilisateurService;
        this.trajetService = trajetService;
    }

    // UC : Evaluer
    @Transactional
    public Evaluation evaluer(EvaluationRequest req) {
        if (req.note() == null || req.note() < 1 || req.note() > 5) {
            throw ApiException.invalide("La note doit etre comprise entre 1 et 5");
        }
        if (req.idAuteur().equals(req.idCible())) {
            throw ApiException.invalide("Vous ne pouvez pas vous evaluer vous-meme");
        }
        if (evaluationRepository.existsByAuteurIdUtilisateurAndCibleIdUtilisateurAndTrajetIdTrajet(
                req.idAuteur(), req.idCible(), req.idTrajet())) {
            throw ApiException.conflit("Vous avez deja evalue cette personne pour ce trajet");
        }
        Utilisateur auteur = utilisateurService.trouver(req.idAuteur());
        Utilisateur cible = utilisateurService.trouver(req.idCible());
        Trajet trajet = trajetService.trouver(req.idTrajet());

        Evaluation e = new Evaluation();
        e.setAuteur(auteur);
        e.setCible(cible);
        e.setTrajet(trajet);
        e.setNote(req.note());
        e.setCommentaire(req.commentaire());
        Evaluation saved = evaluationRepository.save(e);
        utilisateurService.ajouterNote(cible, req.note());
        return saved;
    }

    public List<Evaluation> parCible(Long idCible) {
        return evaluationRepository.findByCibleIdUtilisateur(idCible);
    }
}
