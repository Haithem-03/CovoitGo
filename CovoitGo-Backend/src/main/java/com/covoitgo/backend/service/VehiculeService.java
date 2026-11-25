package com.covoitgo.backend.service;

import com.covoitgo.backend.exception.ApiException;
import com.covoitgo.backend.model.Role;
import com.covoitgo.backend.model.Utilisateur;
import com.covoitgo.backend.model.Vehicule;
import com.covoitgo.backend.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurService utilisateurService;

    public VehiculeService(VehiculeRepository vehiculeRepository, UtilisateurService utilisateurService) {
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurService = utilisateurService;
    }

    // UC : Enregistrer son vehicule
    public Vehicule enregistrer(Long idConducteur, Vehicule v) {
        Utilisateur conducteur = utilisateurService.trouver(idConducteur);
        if (conducteur.getRole() == Role.ADMIN) {
            throw ApiException.interdit("L'administrateur ne peut pas enregistrer de vehicule");
        }
        if (v.getMarque() == null || v.getMarque().isBlank()
                || v.getImmatriculation() == null || v.getImmatriculation().isBlank()) {
            throw ApiException.invalide("Marque et immatriculation obligatoires");
        }
        if (v.getNbPlaces() == null || v.getNbPlaces() < 1 || v.getNbPlaces() > 8) {
            throw ApiException.invalide("Le nombre de places doit etre entre 1 et 8");
        }
        v.setIdVehicule(null);
        v.setConducteur(utilisateurService.devenirConducteur(conducteur));
        return vehiculeRepository.save(v);
    }

    public List<Vehicule> parConducteur(Long idConducteur) {
        return vehiculeRepository.findByConducteurIdUtilisateur(idConducteur);
    }
}
