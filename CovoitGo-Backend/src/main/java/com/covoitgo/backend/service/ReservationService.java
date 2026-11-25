package com.covoitgo.backend.service;

import com.covoitgo.backend.dto.ReservationRequest;
import com.covoitgo.backend.exception.ApiException;
import com.covoitgo.backend.model.*;
import com.covoitgo.backend.repository.ReservationRepository;
import com.covoitgo.backend.repository.TrajetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TrajetRepository trajetRepository;
    private final TrajetService trajetService;
    private final UtilisateurService utilisateurService;

    public ReservationService(ReservationRepository reservationRepository, TrajetRepository trajetRepository,
                              TrajetService trajetService, UtilisateurService utilisateurService) {
        this.reservationRepository = reservationRepository;
        this.trajetRepository = trajetRepository;
        this.trajetService = trajetService;
        this.utilisateurService = utilisateurService;
    }

    // UC : Reserver une place
    public Reservation reserver(ReservationRequest req) {
        Utilisateur passager = utilisateurService.trouver(req.idPassager());
        Trajet trajet = trajetService.trouver(req.idTrajet());
        int nb = req.nbPlaces() == null ? 1 : req.nbPlaces();

        if (passager.getRole() == Role.ADMIN) {
            throw ApiException.interdit("L'administrateur ne peut pas reserver");
        }
        if (trajet.getConducteur().getIdUtilisateur().equals(passager.getIdUtilisateur())) {
            throw ApiException.interdit("Vous ne pouvez pas reserver votre propre trajet");
        }
        if (trajet.getStatut() != StatutTrajet.OUVERT) {
            throw ApiException.conflit("Ce trajet n'est plus disponible");
        }
        if (nb < 1 || nb > trajet.getPlacesRestantes()) {
            throw ApiException.conflit("Places insuffisantes (restantes : " + trajet.getPlacesRestantes() + ")");
        }
        if (reservationRepository.existsByPassagerIdUtilisateurAndTrajetIdTrajetAndStatutIn(
                passager.getIdUtilisateur(), trajet.getIdTrajet(),
                List.of(StatutReservation.EN_ATTENTE, StatutReservation.ACCEPTEE))) {
            throw ApiException.conflit("Vous avez deja une reservation pour ce trajet");
        }
        Reservation r = new Reservation();
        r.setPassager(passager);
        r.setTrajet(trajet);
        r.setNbPlaces(nb);
        r.setStatut(StatutReservation.EN_ATTENTE);
        r.setDateReservation(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return reservationRepository.save(r);
    }

    public List<Reservation> parPassager(Long idPassager) {
        return reservationRepository.findByPassagerIdUtilisateurOrderByIdReservationDesc(idPassager);
    }

    public List<Reservation> parConducteur(Long idConducteur) {
        return reservationRepository.findByTrajetConducteurIdUtilisateurOrderByIdReservationDesc(idConducteur);
    }

    private Reservation trouver(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> ApiException.introuvable("Reservation introuvable"));
    }

    // UC : Traiter une reservation (conducteur)
    @Transactional
    public Reservation accepter(Long id) {
        Reservation r = trouver(id);
        if (r.getStatut() != StatutReservation.EN_ATTENTE) {
            throw ApiException.conflit("Cette reservation a deja ete traitee");
        }
        Trajet t = r.getTrajet();
        if (r.getNbPlaces() > t.getPlacesRestantes()) {
            throw ApiException.conflit("Places insuffisantes sur ce trajet");
        }
        t.setPlacesRestantes(t.getPlacesRestantes() - r.getNbPlaces());
        if (t.getPlacesRestantes() == 0) {
            t.setStatut(StatutTrajet.COMPLET);
        }
        trajetRepository.save(t);
        r.setStatut(StatutReservation.ACCEPTEE);
        return reservationRepository.save(r);
    }

    public Reservation refuser(Long id) {
        Reservation r = trouver(id);
        if (r.getStatut() != StatutReservation.EN_ATTENTE) {
            throw ApiException.conflit("Cette reservation a deja ete traitee");
        }
        r.setStatut(StatutReservation.REFUSEE);
        return reservationRepository.save(r);
    }

    // UC : Gerer les reservations (passager)
    @Transactional
    public Reservation annuler(Long id) {
        Reservation r = trouver(id);
        if (r.getStatut() == StatutReservation.ACCEPTEE) {
            Trajet t = r.getTrajet();
            t.setPlacesRestantes(t.getPlacesRestantes() + r.getNbPlaces());
            if (t.getStatut() == StatutTrajet.COMPLET) {
                t.setStatut(StatutTrajet.OUVERT);
            }
            trajetRepository.save(t);
        } else if (r.getStatut() != StatutReservation.EN_ATTENTE) {
            throw ApiException.conflit("Cette reservation ne peut plus etre annulee");
        }
        r.setStatut(StatutReservation.ANNULEE);
        return reservationRepository.save(r);
    }
}
