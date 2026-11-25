package com.covoitgo.backend.controller;

import com.covoitgo.backend.dto.PrixEstime;
import com.covoitgo.backend.dto.TrajetRequest;
import com.covoitgo.backend.model.Trajet;
import com.covoitgo.backend.service.TrajetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trajets")
public class TrajetController {

    private final TrajetService trajetService;

    public TrajetController(TrajetService trajetService) {
        this.trajetService = trajetService;
    }

    @GetMapping("/estimer")
    public PrixEstime estimer(@RequestParam String depart, @RequestParam String arrivee) {
        return trajetService.estimerPrix(depart, arrivee);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Trajet publier(@RequestBody TrajetRequest request) {
        return trajetService.publier(request);
    }

    @GetMapping("/recherche")
    public List<Trajet> rechercher(@RequestParam(required = false) String depart,
                                   @RequestParam(required = false) String arrivee,
                                   @RequestParam(required = false) String date,
                                   @RequestParam(required = false) Integer places,
                                   @RequestParam(required = false) Long exclure) {
        return trajetService.rechercher(depart, arrivee, date, places, exclure);
    }

    @GetMapping("/{id}")
    public Trajet trouver(@PathVariable Long id) {
        return trajetService.trouver(id);
    }

    @GetMapping("/conducteur/{idConducteur}")
    public List<Trajet> parConducteur(@PathVariable Long idConducteur) {
        return trajetService.parConducteur(idConducteur);
    }

    @PutMapping("/{id}/annuler")
    public Trajet annuler(@PathVariable Long id) {
        return trajetService.annuler(id);
    }
}
