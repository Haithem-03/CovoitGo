package com.covoitgo.backend.repository;

import com.covoitgo.backend.model.Role;
import com.covoitgo.backend.model.StatutConducteur;
import com.covoitgo.backend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Utilisateur> findByRoleAndStatutConducteur(Role role, StatutConducteur statut);
}
