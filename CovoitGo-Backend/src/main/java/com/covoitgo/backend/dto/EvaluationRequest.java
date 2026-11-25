package com.covoitgo.backend.dto;

public record EvaluationRequest(Long idAuteur, Long idCible, Long idTrajet, Integer note, String commentaire) {}
