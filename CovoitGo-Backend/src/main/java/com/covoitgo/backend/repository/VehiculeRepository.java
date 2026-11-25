package com.covoitgo.backend.repository;

import com.covoitgo.backend.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    List<Vehicule> findByConducteurIdUtilisateur(Long idConducteur);
}
