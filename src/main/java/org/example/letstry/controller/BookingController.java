package org.example.letstry.controller;

import org.example.letstry.model.Reservation;
import org.example.letstry.model.ReservationStatus;
import org.example.letstry.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final ReservationRepository reservationRepository;

    public BookingController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> getreservationStatus(@PathVariable Long id) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reservation not found"));
        }

        ReservationStatus status = reservationOptional.get().getStatus();
        return ResponseEntity.ok(Map.of("status", status.name()));
    }
}
