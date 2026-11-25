package com.covoitgo.backend.controller;

import com.covoitgo.backend.dto.EvaluationRequest;
import com.covoitgo.backend.model.Evaluation;
import com.covoitgo.backend.service.EvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evaluation evaluer(@RequestBody EvaluationRequest request) {
        return evaluationService.evaluer(request);
    }

    @GetMapping("/utilisateur/{idUtilisateur}")
    public List<Evaluation> parUtilisateur(@PathVariable Long idUtilisateur) {
        return evaluationService.parCible(idUtilisateur);
    }
}
