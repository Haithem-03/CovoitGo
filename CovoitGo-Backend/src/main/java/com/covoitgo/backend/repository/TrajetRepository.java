package com.covoitgo.backend.repository;

import com.covoitgo.backend.model.StatutTrajet;
import com.covoitgo.backend.model.Trajet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrajetRepository extends JpaRepository<Trajet, Long> {
    List<Trajet> findByStatutOrderByDateDepartAsc(StatutTrajet statut);
    List<Trajet> findByConducteurIdUtilisateurOrderByDateDepartDesc(Long idConducteur);
}
