package com.covoitgo.backend.dto;

/** Reponse de l'estimation : prix conseille et prix maximum autorise par place. */
public record PrixEstime(Double distanceKm, Double prixConseille, Double prixMax, Integer dureeMinutes) {}
