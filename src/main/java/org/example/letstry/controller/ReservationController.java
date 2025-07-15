package org.example.letstry.controller;

import org.example.letstry.model.Hall;
import org.example.letstry.model.Reservation;
import org.example.letstry.model.User;
import org.example.letstry.repository.HallRepository;
import org.example.letstry.repository.ReservationRepository;
import org.example.letstry.service.GraphTokenService;
import org.example.letstry.service.HallService;
import org.example.letstry.service.ReservationService;
import org.example.letstry.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HallService hallService;
    private final UserService userService;
    private final GraphTokenService graphTokenService;

    public ReservationController(
            ReservationService reservationService,
            HallService hallService,
            UserService userService,
            GraphTokenService graphTokenService,
            ReservationRepository reservationRepository,
            HallRepository hallRepository
    ) {
        this.reservationService = reservationService;
        this.hallService = hallService;
        this.userService = userService;
        this.graphTokenService = graphTokenService;
    }

    @GetMapping("/hall/{hallId}")
    public List<Map<String, Object>> getReservationsByHall(@PathVariable Long hallId) {
        return reservationService.getOutlookReservationsByHall(hallId);
    }

    @PostMapping("/hall/{hallId}/reserve")
    public ResponseEntity<Map<String, Object>> addReservation(
            @PathVariable Long hallId,
            @RequestBody Reservation reservation,
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient) {

        String userEmail = principal.getAttribute("email");
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        if (userEmail == null || accessToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Brak autoryzacji użytkownika."));
        }

        try {
            User user = userService.createUserIfNotExists(principal);
            Hall hall = hallService.findHallById(hallId)
                    .orElseThrow(() -> new IllegalArgumentException("Sala nie istnieje: " + hallId));

            if (reservationService.isReservedAtThisTime(hallId, reservation)) {
                return ResponseEntity.status(409).body(Map.of("error", "Sala jest już zarezerwowana w podanym czasie."));
            }

            reservationService.createReservation(hall, user, reservation);
            boolean confirmed = reservationService.createOutlookEventForUser(accessToken, reservation, hall);

            return ResponseEntity.ok(Map.of(
                    "bookingId", reservation.getId(),
                    "status", confirmed ? "CONFIRMED" : "PENDING"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Błąd serwera: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable String eventId,
            @RequestParam String hallEmail,
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient) {

        String userEmail = principal.getAttribute("email");
        String token = authorizedClient.getAccessToken().getTokenValue();

        Optional<Reservation> resOpt = reservationService.findByOutlookEventId(eventId);

        if (resOpt.isPresent()) {
            Reservation reservation = resOpt.get();
            if (!reservation.getOrganizerEmail().equalsIgnoreCase(userEmail)) {
                return ResponseEntity.status(403).body("Nie jesteś właścicielem rezerwacji.");
            }

            boolean deleted = reservationService.deleteEventFromUserCalendar(eventId, token);
            if (deleted) {
                reservationService.removeReservationFromDb(eventId);
                return ResponseEntity.ok("Usunięto wydarzenie.");
            } else {
                return ResponseEntity.status(500).body("Nie udało się usunąć wydarzenia z kalendarza użytkownika.");
            }
        }

        boolean deletedFromRoom = reservationService.deleteEventFromRoomCalendarIfOwner(eventId, userEmail, hallEmail);
        if (deletedFromRoom) {
            reservationService.removeReservationFromDb(eventId);
            return ResponseEntity.ok("Usunięto wydarzenie z kalendarza sali.");
        }

        return ResponseEntity.status(404).body("Nie znaleziono wydarzenia lub brak uprawnień.");
    }
}
