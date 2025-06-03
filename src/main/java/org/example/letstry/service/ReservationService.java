package org.example.letstry.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.letstry.model.Hall;
import org.example.letstry.model.Reservation;
import org.example.letstry.model.User;
import org.example.letstry.repository.ReservationRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HallService hallService;
    private final GraphTokenService graphTokenService;
    private final WebClient webClient;

    public ReservationService(
            ReservationRepository reservationRepository,
            HallService hallService,
            GraphTokenService graphTokenService,
            WebClient webClient
    ) {
        this.reservationRepository = reservationRepository;
        this.hallService = hallService;
        this.graphTokenService = graphTokenService;
        this.webClient = webClient;
    }

    public List<Map<String, Object>> getOutlookReservationsByHall(Long hallId) {
        Hall hall = hallService.findHallById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Sala o ID " + hallId + " nie istnieje!"));

        String salaEmail = hall.getEmail();
        String accessToken = graphTokenService.getAppAccessToken();

        System.out.println("[DEBUG] Pobieram wydarzenia z kalendarza sali: " + salaEmail);

        JsonNode eventsNode = webClient.get()
                .uri("/users/" + salaEmail + "/calendar/events")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<Map<String, Object>> results = new ArrayList<>();
        if (eventsNode != null && eventsNode.has("value")) {
            for (JsonNode event : eventsNode.get("value")) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", event.get("id").asText());
                map.put("title", event.get("subject").asText());
                map.put("start", event.get("start").get("dateTime").asText());
                map.put("end", event.get("end").get("dateTime").asText());
                map.put("email", event.get("organizer").get("emailAddress").get("address").asText());
                results.add(map);
            }
            System.out.println("[DEBUG] Znaleziono wydarzeń: " + results.size());
        } else {
            System.out.println("[WARN] Brak wydarzeń lub odpowiedź bez pola 'value'.");
        }

        return results;
    }

    public void createOutlookEventForUser(String accessToken, Reservation reservation, Hall hall) {
        Map<String, Object> event = Map.of(
                "subject", "Rezerwacja sali: " + hall.getName(),
                "location", Map.of("displayName", hall.getName()),
                "start", Map.of("dateTime", reservation.getStartMeeting().toString(), "timeZone", "UTC"),
                "end", Map.of("dateTime", reservation.getEndMeeting().toString(), "timeZone", "UTC"),
                "attendees", List.of(
                        Map.of("emailAddress", Map.of("address", hall.getEmail()), "type", "required")
                )
        );

        System.out.println("[DEBUG] Tworzę wydarzenie w kalendarzu użytkownika z zaproszeniem sali: " + hall.getEmail());

        webClient.post()
                .uri("/me/events")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(event)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public boolean deleteEventFromUserCalendar(String eventId, String accessToken) {
        try {
            System.out.println("[DEBUG] Usuwam wydarzenie z kalendarza użytkownika: " + eventId);
            webClient.delete()
                    .uri("/me/events/" + eventId)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            System.out.println("[ERROR] Nie udało się usunąć wydarzenia: " + e.getStatusCode() + " - " + e.getMessage());
            return false;
        }
    }

    public boolean isReservedAtThisTime(Long hallId, Reservation reservation) {
        boolean conflict = !reservationRepository.findByHallIdAndStartMeetingBeforeAndEndMeetingAfter(
                hallId,
                reservation.getEndMeeting(),
                reservation.getStartMeeting()
        ).isEmpty();
        System.out.println("[DEBUG] Sprawdzenie konfliktu rezerwacji: " + conflict);
        return conflict;
    }

    public void createReservation(Hall hall, User user, Reservation reservation) {
        reservation.setHall(hall);
        reservation.setUser(user);
        reservation.setDate(Instant.now());
        reservationRepository.save(reservation);
        System.out.println("[DEBUG] Zapisano rezerwację w bazie danych.");
    }

    public Optional<Reservation> findReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public void updateReservation(Reservation updated, Reservation existing) {
        existing.setDate(updated.getDate());
        existing.setStartMeeting(updated.getStartMeeting());
        existing.setEndMeeting(updated.getEndMeeting());
        reservationRepository.save(existing);
    }

    public List<Map<String, Object>> getUserEventsWithRoom(String userAccessToken, String roomEmail) {
        System.out.println("[DEBUG] Szukam wydarzeń użytkownika zawierających salę: " + roomEmail);

        JsonNode eventsNode = webClient.get()
                .uri("/me/events")
                .headers(headers -> headers.setBearerAuth(userAccessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        List<Map<String, Object>> results = new ArrayList<>();
        if (eventsNode != null && eventsNode.has("value")) {
            for (JsonNode event : eventsNode.get("value")) {
                for (JsonNode attendee : event.withArray("attendees")) {
                    String address = attendee.get("emailAddress").get("address").asText();
                    if (address.equalsIgnoreCase(roomEmail)) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", event.get("id").asText());
                        map.put("title", event.get("subject").asText());
                        map.put("start", event.get("start").get("dateTime").asText());
                        map.put("end", event.get("end").get("dateTime").asText());
                        map.put("userEmail", event.get("organizer").get("emailAddress").get("address").asText());
                        results.add(map);
                        break;
                    }
                }
            }
            System.out.println("[DEBUG] Znaleziono wydarzeń użytkownika z salą: " + results.size());
        }
        return results;
    }
    public boolean deleteEventFromRoomCalendarIfOwner(String eventId, String userEmail, String hallEmail) {
        try {
            String token = graphTokenService.getAppAccessToken();

            JsonNode event = webClient.get()
                    .uri("/users/" + hallEmail + "/events/" + eventId)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            String organizer = event.get("organizer").get("emailAddress").get("address").asText();
            System.out.println("[DEBUG] Organizer from hall calendar: " + organizer);

            if (!organizer.equalsIgnoreCase(userEmail)) {
                System.out.println("[INFO] Event found in hall calendar, but organizer is not the current user.");
                return false;
            }

            webClient.delete()
                    .uri("/users/" + hallEmail + "/events/" + eventId)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            System.out.println("[DEBUG] Successfully deleted from hall calendar.");
            return true;

        } catch (WebClientResponseException.NotFound nf) {
            System.out.println("[WARN] Not found in hall calendar.");
            return false;
        } catch (Exception e) {
            System.out.println("[ERROR] Error during hall calendar delete: " + e.getMessage());
            return false;
        }
    }
}