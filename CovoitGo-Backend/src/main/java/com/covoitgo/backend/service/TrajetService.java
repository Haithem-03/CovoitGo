package com.covoitgo.backend.service;

import com.covoitgo.backend.dto.PrixEstime;
import com.covoitgo.backend.dto.TrajetRequest;
import com.covoitgo.backend.exception.ApiException;
import com.covoitgo.backend.model.*;
import com.covoitgo.backend.repository.ReservationRepository;
import com.covoitgo.backend.repository.TrajetRepository;
import com.covoitgo.backend.repository.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class TrajetService {

    // Prix conseille : 0,150 DT par km et par place (min. 2 DT), arrondi a 0,500 DT.
    // Prix maximum autorise : prix conseille x 4/3, arrondi a 0,500 DT.
    private static final double TARIF_KM = 0.15;
    private static final double PRIX_MIN = 2.0;
    // Duree estimee : 1,3 min par km, arrondie aux 5 minutes (minimum 10 min)
    private static final double MINUTES_PAR_KM = 1.3;
    // Un trajet recurrent est cree pour les 4 semaines a venir
    private static final int JOURS_RECURRENCE = 28;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Map<String, DayOfWeek> JOURS = Map.of(
            "L", DayOfWeek.MONDAY, "MA", DayOfWeek.TUESDAY, "ME", DayOfWeek.WEDNESDAY,
            "J", DayOfWeek.THURSDAY, "V", DayOfWeek.FRIDAY, "S", DayOfWeek.SATURDAY);

    private final TrajetRepository trajetRepository;
    private final VehiculeRepository vehiculeRepository;
    private final ReservationRepository reservationRepository;
    private final UtilisateurService utilisateurService;
    private final LieuService lieuService;

    public TrajetService(TrajetRepository trajetRepository, VehiculeRepository vehiculeRepository,
                         ReservationRepository reservationRepository, UtilisateurService utilisateurService,
                         LieuService lieuService) {
        this.trajetRepository = trajetRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.reservationRepository = reservationRepository;
        this.utilisateurService = utilisateurService;
        this.lieuService = lieuService;
    }

    // UC : Estimer le prix de la place
    public PrixEstime estimerPrix(String depart, String arrivee) {
        if (vide(depart) || vide(arrivee)) {
            throw ApiException.invalide("Depart et arrivee sont obligatoires");
        }
        double km = lieuService.distance(depart, arrivee);
        double conseille = arrondi(Math.max(PRIX_MIN, km * TARIF_KM));
        double max = arrondi(conseille * 4 / 3);
        int duree = (int) Math.max(10, Math.round(km * MINUTES_PAR_KM / 5) * 5);
        return new PrixEstime(km, conseille, max, duree);
    }

    // UC : Gerer les trajets (publier)
    @Transactional
    public Trajet publier(TrajetRequest req) {
        Utilisateur conducteur = utilisateurService.trouver(req.idConducteur());
        if (conducteur.getStatutConducteur() != StatutConducteur.VALIDE) {
            throw ApiException.interdit(conducteur.getStatutConducteur() == StatutConducteur.EN_ATTENTE
                    ? "Votre compte conducteur est en attente de validation"
                    : "Enregistrez d'abord votre vehicule");
        }
        List<Vehicule> vehicules = vehiculeRepository.findByConducteurIdUtilisateur(conducteur.getIdUtilisateur());
        if (vehicules.isEmpty()) {
            throw ApiException.interdit("Enregistrez d'abord votre vehicule");
        }
        if (vide(req.villeDepart()) || vide(req.villeArrivee()) || vide(req.date()) || vide(req.heure())) {
            throw ApiException.invalide("Depart, arrivee, date et heure sont obligatoires");
        }
        if (LieuService.normaliser(req.villeDepart()).equals(LieuService.normaliser(req.villeArrivee()))) {
            throw ApiException.invalide("Le depart et l'arrivee doivent etre differents");
        }
        Vehicule vehicule = vehicules.get(vehicules.size() - 1);
        if (req.nbPlaces() == null || req.nbPlaces() < 1 || req.nbPlaces() > vehicule.getNbPlaces()) {
            throw ApiException.invalide("Places proposees : entre 1 et " + vehicule.getNbPlaces());
        }

        LocalDate premierJour;
        LocalTime heure;
        try {
            premierJour = LocalDate.parse(req.date().trim());
            heure = LocalTime.parse(req.heure().trim());
        } catch (DateTimeParseException e) {
            throw ApiException.invalide("Date ou heure invalide");
        }
        if (LocalDateTime.of(premierJour, heure).isBefore(LocalDateTime.now())) {
            throw ApiException.invalide("La date de depart est deja passee");
        }

        PrixEstime estimation = estimerPrix(req.villeDepart(), req.villeArrivee());
        Double prix = req.prixParPlace();
        if (prix == null || prix <= 0) {
            prix = estimation.prixConseille();
        }
        if (prix > estimation.prixMax()) {
            throw ApiException.invalide("Le prix maximum pour ce trajet est de " + estimation.prixMax() + " DT");
        }

        // Dates a creer : une seule, ou chaque jour coche pendant 4 semaines
        Set<DayOfWeek> jours = new HashSet<>();
        if (req.jours() != null) {
            for (String j : req.jours()) {
                DayOfWeek d = JOURS.get(j);
                if (d != null) jours.add(d);
            }
        }
        List<LocalDate> dates = new ArrayList<>();
        if (jours.isEmpty()) {
            dates.add(premierJour);
        } else {
            for (int i = 0; i < JOURS_RECURRENCE; i++) {
                LocalDate d = premierJour.plusDays(i);
                if (jours.contains(d.getDayOfWeek())) dates.add(d);
            }
            if (dates.isEmpty()) dates.add(premierJour);
        }
        String recurrence = req.jours() == null ? null : String.join(",", req.jours());

        Trajet premier = null;
        for (LocalDate d : dates) {
            LocalDateTime depart = LocalDateTime.of(d, heure);
            Trajet t = new Trajet();
            t.setConducteur(conducteur);
            t.setVehicule(vehicule);
            t.setVilleDepart(req.villeDepart().trim());
            t.setVilleArrivee(req.villeArrivee().trim());
            t.setDateDepart(depart.format(FORMAT));
            t.setHeureArrivee(depart.plusMinutes(estimation.dureeMinutes()).toLocalTime().toString());
            t.setDistanceKm(estimation.distanceKm());
            t.setNbPlaces(req.nbPlaces());
            t.setPlacesRestantes(req.nbPlaces());
            t.setPrixParPlace(prix);
            t.setJoursRecurrence(jours.isEmpty() ? null : recurrence);
            t.setStatut(StatutTrajet.OUVERT);
            Trajet saved = trajetRepository.save(t);
            if (premier == null) premier = saved;
        }
        return premier;
    }

    // UC : Reserver une place (recherche)
    public List<Trajet> rechercher(String depart, String arrivee, String date, Integer places, Long exclure) {
        String maintenant = LocalDateTime.now().format(FORMAT);
        int nb = places == null || places < 1 ? 1 : places;
        return trajetRepository.findByStatutOrderByDateDepartAsc(StatutTrajet.OUVERT).stream()
                .filter(t -> t.getDateDepart().compareTo(maintenant) >= 0)
                .filter(t -> t.getPlacesRestantes() >= nb)
                .filter(t -> vide(date) || t.getDateDepart().startsWith(date.trim()))
                .filter(t -> correspond(t.getVilleDepart(), depart))
                .filter(t -> correspond(t.getVilleArrivee(), arrivee))
                .filter(t -> exclure == null || !t.getConducteur().getIdUtilisateur().equals(exclure))
                .toList();
    }

    public List<Trajet> parConducteur(Long idConducteur) {
        return trajetRepository.findByConducteurIdUtilisateurOrderByDateDepartDesc(idConducteur);
    }

    public Trajet trouver(Long id) {
        return trajetRepository.findById(id).orElseThrow(() -> ApiException.introuvable("Trajet introuvable"));
    }

    @Transactional
    public Trajet annuler(Long idTrajet) {
        Trajet t = trouver(idTrajet);
        t.setStatut(StatutTrajet.ANNULE);
        for (Reservation r : reservationRepository.findByTrajetIdTrajet(idTrajet)) {
            if (r.getStatut() == StatutReservation.EN_ATTENTE || r.getStatut() == StatutReservation.ACCEPTEE) {
                r.setStatut(StatutReservation.ANNULEE);
                reservationRepository.save(r);
            }
        }
        return trajetRepository.save(t);
    }

    /** "Esprit" correspond a "Esprit · El Ghazala" ; recherche sans accents ni majuscules. */
    private boolean correspond(String lieuTrajet, String recherche) {
        if (vide(recherche)) return true;
        if (LieuService.estEsprit(recherche)) return LieuService.estEsprit(lieuTrajet);
        String r = LieuService.normaliser(recherche);
        for (String morceau : r.split(",")) {
            if (!morceau.isBlank() && LieuService.normaliser(lieuTrajet).contains(morceau.trim())) return true;
        }
        return false;
    }

    private double arrondi(double prix) {
        return Math.round(prix * 2) / 2.0;
    }

    private boolean vide(String s) {
        return s == null || s.trim().isEmpty();
    }
}
