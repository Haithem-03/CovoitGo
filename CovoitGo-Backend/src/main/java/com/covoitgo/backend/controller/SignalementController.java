package com.covoitgo.backend.controller;

import com.covoitgo.backend.dto.SignalementRequest;
import com.covoitgo.backend.model.Signalement;
import com.covoitgo.backend.service.SignalementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signalements")
public class SignalementController {

    private final SignalementService signalementService;

    public SignalementController(SignalementService signalementService) {
        this.signalementService = signalementService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Signalement signaler(@RequestBody SignalementRequest request) {
        return signalementService.signaler(request);
    }
}
