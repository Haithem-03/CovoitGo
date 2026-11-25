package com.covoitgo.backend.dto;

import java.util.List;

/**
 * Publication d'un trajet (ecran "Publier un trajet").
 * date = "aaaa-mm-jj", heure = "hh:mm", jours = ["L","MA","ME","J","V","S"] si trajet recurrent.
 */
public record TrajetRequest(Long idConducteur, String villeDepart, String villeArrivee, String date, String heure,
                            Integer nbPlaces, Double prixParPlace, List<String> jours) {}
