package com.covoitgo.backend.controller;

import com.covoitgo.backend.dto.LoginRequest;
import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public Utilisateur inscrire(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.inscrire(utilisateur);
    }

    @PostMapping("/login")
    public Utilisateur login(@RequestBody LoginRequest request) {
        return utilisateurService.login(request);
    }
}
