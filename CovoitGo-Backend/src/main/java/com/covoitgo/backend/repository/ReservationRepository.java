package com.covoitgo.backend.repository;

import com.covoitgo.backend.model.Reservation;
import com.covoitgo.backend.model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPassagerIdUtilisateurOrderByIdReservationDesc(Long idPassager);
    List<Reservation> findByTrajetConducteurIdUtilisateurOrderByIdReservationDesc(Long idConducteur);
    List<Reservation> findByTrajetIdTrajet(Long idTrajet);
    boolean existsByPassagerIdUtilisateurAndTrajetIdTrajetAndStatutIn(Long idPassager, Long idTrajet,
                                                                    List<StatutReservation> statuts);
}
