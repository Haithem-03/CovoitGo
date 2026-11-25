package com.example.covoitgo.model;

/** Objets envoyes au backend (corps des requetes POST / PUT). */
public final class Requetes {

    private Requetes() {}

    public static class Login {
        final String email;
        final String motDePasse;
        public Login(String email, String motDePasse) { this.email = email; this.motDePasse = motDePasse; }
    }

    public static class NouveauTrajet {
        final Long idConducteur;
        final String villeDepart;
        final String villeArrivee;
        final String date;          // "aaaa-mm-jj"
        final String heure;         // "hh:mm"
        final Integer nbPlaces;
        final Double prixParPlace;
        final java.util.List<String> jours;  // null si trajet unique

        public NouveauTrajet(Long idConducteur, String villeDepart, String villeArrivee, String date, String heure,
                             Integer nbPlaces, Double prixParPlace, java.util.List<String> jours) {
            this.idConducteur = idConducteur;
            this.villeDepart = villeDepart;
            this.villeArrivee = villeArrivee;
            this.date = date;
            this.heure = heure;
            this.nbPlaces = nbPlaces;
            this.prixParPlace = prixParPlace;
            this.jours = jours;
        }
    }

    public static class NouvelleReservation {
        final Long idPassager;
        final Long idTrajet;
        final Integer nbPlaces;
        public NouvelleReservation(Long idPassager, Long idTrajet, Integer nbPlaces) {
            this.idPassager = idPassager; this.idTrajet = idTrajet; this.nbPlaces = nbPlaces;
        }
    }

    public static class NouvelleEvaluation {
        final Long idAuteur;
        final Long idCible;
        final Long idTrajet;
        final Integer note;
        final String commentaire;
        public NouvelleEvaluation(Long idAuteur, Long idCible, Long idTrajet, Integer note, String commentaire) {
            this.idAuteur = idAuteur; this.idCible = idCible; this.idTrajet = idTrajet;
            this.note = note; this.commentaire = commentaire;
        }
    }

    public static class NouveauSignalement {
        final Long idAuteur;
        final Long idCible;
        final String motif;
        public NouveauSignalement(Long idAuteur, Long idCible, String motif) {
            this.idAuteur = idAuteur; this.idCible = idCible; this.motif = motif;
        }
    }

    public static class Decision {
        final String decision;
        public Decision(String decision) { this.decision = decision; }
    }

    /** Reponse de /api/trajets/estimer */
    public static class PrixEstime {
        Double distanceKm;
        Double prixConseille;
        Double prixMax;
        Integer dureeMinutes;
        public Double getPrixConseille() { return prixConseille; }
        public Double getPrixMax() { return prixMax; }
    }
}
