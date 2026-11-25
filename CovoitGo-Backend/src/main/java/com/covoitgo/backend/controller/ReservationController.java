package com.covoitgo.backend.controller;

import com.covoitgo.backend.dto.ReservationRequest;
import com.covoitgo.backend.model.Reservation;
import com.covoitgo.backend.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation reserver(@RequestBody ReservationRequest request) {
        return reservationService.reserver(request);
    }

    @GetMapping("/passager/{idPassager}")
    public List<Reservation> parPassager(@PathVariable Long idPassager) {
        return reservationService.parPassager(idPassager);
    }

    @GetMapping("/conducteur/{idConducteur}")
    public List<Reservation> parConducteur(@PathVariable Long idConducteur) {
        return reservationService.parConducteur(idConducteur);
    }

    @PutMapping("/{id}/accepter")
    public Reservation accepter(@PathVariable Long id) {
        return reservationService.accepter(id);
    }

    @PutMapping("/{id}/refuser")
    public Reservation refuser(@PathVariable Long id) {
        return reservationService.refuser(id);
    }

    @PutMapping("/{id}/annuler")
    public Reservation annuler(@PathVariable Long id) {
        return reservationService.annuler(id);
    }
}
