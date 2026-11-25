package com.covoitgo.backend.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Distances approximatives (km) entre les lieux frequents et Esprit (El Ghazala).
 * Sert a estimer le prix conseille et l'heure d'arrivee. A completer si besoin.
 */
@Service
public class LieuService {

    public static final double DISTANCE_PAR_DEFAUT = 20.0;

    private static final Map<String, Double> DISTANCE_VERS_ESPRIT = new LinkedHashMap<>();

    static {
        DISTANCE_VERS_ESPRIT.put("nouvelle medina", 24.0);
        DISTANCE_VERS_ESPRIT.put("tunis centre", 15.0);
        DISTANCE_VERS_ESPRIT.put("hammam lif", 33.0);
        DISTANCE_VERS_ESPRIT.put("ben arous", 22.0);
        DISTANCE_VERS_ESPRIT.put("la marsa", 18.0);
        DISTANCE_VERS_ESPRIT.put("la soukra", 7.0);
        DISTANCE_VERS_ESPRIT.put("el menzah", 9.0);
        DISTANCE_VERS_ESPRIT.put("el mourouj", 24.0);
        DISTANCE_VERS_ESPRIT.put("ezzahra", 30.0);
        DISTANCE_VERS_ESPRIT.put("manouba", 18.0);
        DISTANCE_VERS_ESPRIT.put("megrine", 22.0);
        DISTANCE_VERS_ESPRIT.put("ariana", 6.0);
        DISTANCE_VERS_ESPRIT.put("ennasr", 6.0);
        DISTANCE_VERS_ESPRIT.put("raoued", 8.0);
        DISTANCE_VERS_ESPRIT.put("bardo", 14.0);
        DISTANCE_VERS_ESPRIT.put("kram", 18.0);
        DISTANCE_VERS_ESPRIT.put("carthage", 17.0);
        DISTANCE_VERS_ESPRIT.put("lac", 11.0);
        DISTANCE_VERS_ESPRIT.put("rades", 25.0);
        DISTANCE_VERS_ESPRIT.put("mornag", 35.0);
        DISTANCE_VERS_ESPRIT.put("tunis", 15.0);
    }

    public static boolean estEsprit(String lieu) {
        return normaliser(lieu).contains("esprit") || normaliser(lieu).contains("ghazala");
    }

    /** Distance estimee entre deux lieux, l'un des deux etant en general Esprit. */
    public double distance(String depart, String arrivee) {
        if (estEsprit(arrivee)) return distanceVersEsprit(depart);
        if (estEsprit(depart)) return distanceVersEsprit(arrivee);
        return DISTANCE_PAR_DEFAUT;
    }

    private double distanceVersEsprit(String lieu) {
        String n = normaliser(lieu);
        for (Map.Entry<String, Double> e : DISTANCE_VERS_ESPRIT.entrySet()) {
            if (n.contains(e.getKey())) return e.getValue();
        }
        return DISTANCE_PAR_DEFAUT;
    }

    public static String normaliser(String s) {
        if (s == null) return "";
        String sansAccents = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sansAccents.toLowerCase().trim();
    }
}
