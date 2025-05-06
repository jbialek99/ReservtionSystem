package org.example.letstry.controller;

import org.example.letstry.model.Hall;
import org.example.letstry.model.Reservation;
import org.example.letstry.model.User;
import org.example.letstry.service.HallService;
import org.example.letstry.service.ReservationService;
import org.example.letstry.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HallService hallService;

    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public ReservationController(ReservationService reservationService, HallService hallService, UserService userService, OAuth2AuthorizedClientService authorizedClientService) {
        this.reservationService = reservationService;
        this.hallService = hallService;
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    // Pobranie rezerwacji dla danej sali
    @GetMapping("/hall/{hallId}")
    public List<Map<String, Object>> getReservationsByHall(@PathVariable Long hallId) {
        return reservationService.getReservationByHall(hallId);
    }

    @PostMapping("/hall/{hallId}/reserve/{userId}")
    public ResponseEntity<String> addReservation(@PathVariable Long hallId,
                                                 @PathVariable Long userId,
                                                 @RequestBody Reservation reservation,
                                                 @AuthenticationPrincipal OAuth2User principal,
                                                 OAuth2AuthenticationToken oauthToken) {

        Hall hall = hallService.findHallById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Sala o ID " + hallId + " nie istnieje!"));
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik o ID " + userId + " nie istnieje!"));

        if (reservationService.isReservedAtThisTime(hallId, reservation)) {
            return ResponseEntity.status(409).body("Sala jest już zarezerwowana w tym czasie!");
        }

        reservationService.createReservation(hall, user, reservation);

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());
        String accessToken = client.getAccessToken().getTokenValue();



        return ResponseEntity.ok("Rezerwacja została zapisana i wydarzenie dodane do kalendarza Outlook!");
    }


    // Aktualizacja rezerwacji po jej przesunięciu lub rozciągnięciu
    @PostMapping("/update")
    public ResponseEntity<String> updateReservation(@RequestBody Reservation updatedReservation) {
        Reservation reservation = reservationService.findReservationById(updatedReservation.getId())
                .orElseThrow(() -> new IllegalArgumentException("Rezerwacja o ID " + updatedReservation.getId() + " nie istnieje!"));
        reservationService.updateReservation(updatedReservation, reservation);
        return ResponseEntity.ok("Rezerwacja została zaktualizowana!");
    }


}
