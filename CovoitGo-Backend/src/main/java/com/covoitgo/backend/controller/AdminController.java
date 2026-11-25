package com.covoitgo.backend.controller;

import com.covoitgo.backend.dto.DecisionRequest;
import com.covoitgo.backend.model.Signalement;
import com.covoitgo.backend.model.StatutConducteur;
import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.service.SignalementService;
import com.covoitgo.backend.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final SignalementService signalementService;

    public AdminController(UtilisateurService utilisateurService, SignalementService signalementService) {
        this.utilisateurService = utilisateurService;
        this.signalementService = signalementService;
    }

    // UC : Valider un conducteur
    @GetMapping("/conducteurs/en-attente")
    public List<Utilisateur> conducteursEnAttente() {
        return utilisateurService.conducteursEnAttente();
    }

    @PutMapping("/conducteurs/{id}/valider")
    public Utilisateur valider(@PathVariable Long id) {
        return utilisateurService.changerStatutConducteur(id, StatutConducteur.VALIDE);
    }

    @PutMapping("/conducteurs/{id}/refuser")
    public Utilisateur refuser(@PathVariable Long id) {
        return utilisateurService.changerStatutConducteur(id, StatutConducteur.REFUSE);
    }

    // UC : Gerer les reclamations
    @GetMapping("/signalements")
    public List<Signalement> signalements() {
        return signalementService.tous();
    }

    @PutMapping("/signalements/{id}/traiter")
    public Signalement traiter(@PathVariable Long id, @RequestBody DecisionRequest request) {
        return signalementService.traiter(id, request.decision());
    }
}
