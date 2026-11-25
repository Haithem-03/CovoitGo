package com.covoitgo.backend.repository;

import com.covoitgo.backend.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByCibleIdUtilisateur(Long idCible);
    boolean existsByAuteurIdUtilisateurAndCibleIdUtilisateurAndTrajetIdTrajet(Long idAuteur, Long idCible, Long idTrajet);
}
