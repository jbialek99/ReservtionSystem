package org.example.letstry.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.letstry.model.Hall;
import org.example.letstry.model.Reservation;
import org.example.letstry.model.User;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final HallService hallService;
    private final UserService userService;
    private final WebClient webClient;
    private final GraphTokenService graphTokenService;

    public ReservationController(ReservationService reservationService, HallService hallService,
                                 UserService userService, WebClient webClient, GraphTokenService graphTokenService) {
        this.reservationService = reservationService;
        this.hallService = hallService;
        this.userService = userService;
        this.webClient = webClient;
        this.graphTokenService = graphTokenService;
    }

    @GetMapping("/hall/{hallId}")
    public List<Map<String, Object>> getReservationsByHall(@PathVariable Long hallId) {
        return reservationService.getOutlookReservationsByHall(hallId);
    }

    @PostMapping("/hall/{hallId}/reserve")
    public ResponseEntity<String> addReservation(@PathVariable Long hallId,
                                                 @RequestBody Reservation reservation,
                                                 @AuthenticationPrincipal OAuth2User principal,
                                                 @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient) {
        String userEmail = principal.getAttribute("email");
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        if (userEmail == null || accessToken == null) {
            return ResponseEntity.badRequest().body("Brak autoryzacji użytkownika.");
        }

        User user = userService.findUserByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika: " + userEmail));
        Hall hall = hallService.findHallById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Sala nie istnieje: " + hallId));

        if (reservationService.isReservedAtThisTime(hallId, reservation)) {
            return ResponseEntity.status(409).body("Sala jest już zarezerwowana w podanym czasie.");
        }

        try {
            reservationService.createReservation(hall, user, reservation);
            reservationService.createOutlookEventForUser(accessToken, reservation, hall);
            return ResponseEntity.ok("Rezerwacja została pomyślnie zapisana.");
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Błąd Outlooka: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Nie udało się zapisać rezerwacji. Spróbuj ponownie.");
        }
    }

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<String> deleteReservation(
            @PathVariable String eventId,
            @RequestParam String hallEmail,
            @AuthenticationPrincipal OAuth2User principal,
            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient authorizedClient
    ) {
        String userEmail = principal.getAttribute("email");
        String userAccessToken = authorizedClient.getAccessToken().getTokenValue();

        System.out.println("\n=== DELETE ===");
        System.out.println("Zalogowany: " + userEmail);
        System.out.println("Event ID: " + eventId);
        System.out.println("Sala (kalendarz): " + hallEmail);

        try {
            JsonNode userEvent = webClient.get()
                    .uri("/me/events/" + eventId)
                    .headers(h -> h.setBearerAuth(userAccessToken))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String organizer = userEvent.get("organizer").get("emailAddress").get("address").asText();
            System.out.println("Organizator (user): " + organizer);

            if (!organizer.equalsIgnoreCase(userEmail)) {
                System.out.println("Nie jesteś organizatorem");
                return ResponseEntity.status(403).body("Nie jesteś organizatorem wydarzenia.");
            }

            webClient.delete()
                    .uri("/me/events/" + eventId)
                    .headers(h -> h.setBearerAuth(userAccessToken))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            System.out.println("✅ Usunięto z /me/events");
            return ResponseEntity.ok("Usunięto wydarzenie");

        } catch (WebClientResponseException.NotFound notFound) {
            System.out.println("❌ Nie znaleziono w /me/events – próbuję z kalendarza sali...");

            boolean deletedFromRoom = reservationService.deleteEventFromRoomCalendarIfOwner(eventId, userEmail, hallEmail);
            if (deletedFromRoom) {
                return ResponseEntity.ok("Usunięto wydarzenie z kalendarza sali.");
            }

            return ResponseEntity.status(404).body("Nie znaleziono wydarzenia lub brak uprawnień do usunięcia.");
        }
    }
}
