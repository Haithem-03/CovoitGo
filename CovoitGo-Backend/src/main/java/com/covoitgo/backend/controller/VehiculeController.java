package com.covoitgo.backend.controller;

import com.covoitgo.backend.model.Vehicule;
import com.covoitgo.backend.service.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @PostMapping("/conducteur/{idConducteur}")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule enregistrer(@PathVariable Long idConducteur, @RequestBody Vehicule vehicule) {
        return vehiculeService.enregistrer(idConducteur, vehicule);
    }

    @GetMapping("/conducteur/{idConducteur}")
    public List<Vehicule> parConducteur(@PathVariable Long idConducteur) {
        return vehiculeService.parConducteur(idConducteur);
    }
}
