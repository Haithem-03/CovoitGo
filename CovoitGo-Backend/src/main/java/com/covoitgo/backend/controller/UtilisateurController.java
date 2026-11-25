package com.covoitgo.backend.controller;

import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/{id}")
    public Utilisateur trouver(@PathVariable Long id) {
        return utilisateurService.trouver(id);
    }

    @PutMapping("/{id}")
    public Utilisateur modifier(@PathVariable Long id, @RequestBody Utilisateur donnees) {
        return utilisateurService.modifierProfil(id, donnees);
    }
}
