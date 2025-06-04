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
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HallService hallService;
    private final GraphTokenService graphTokenService;
    private final WebClient webClient;
    private final UserService userService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSS]").withZone(ZoneOffset.UTC);


    public ReservationService(
            ReservationRepository reservationRepository,
            HallService hallService,
            GraphTokenService graphTokenService,
            WebClient webClient, UserService userService
    ) {
        this.reservationRepository = reservationRepository;
        this.hallService = hallService;
        this.graphTokenService = graphTokenService;
        this.webClient = webClient;
        this.userService = userService;
    }

    public List<Map<String, Object>> getOutlookReservationsByHall(Long hallId) {
        Hall hall = hallService.findHallById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Sala o ID " + hallId + " nie istnieje!"));

        String salaEmail = hall.getEmail();
        String accessToken = graphTokenService.getAppAccessToken();

        // 1. Pobierz z bazy tylko te, które NIE mają eventId (czyli nie zaakceptowane przez salę)
        List<Reservation> localReservations = reservationRepository.findByHallId(hallId)
                .stream()
                .filter(r -> r.getOutlookEventId() == null)
                .toList();

        Set<String> addedIds = new HashSet<>();
        List<Map<String, Object>> merged = new ArrayList<>();

        // 2. Dodaj z Outlooka
        JsonNode eventsNode = webClient.get()
                .uri("/users/" + salaEmail + "/calendar/events")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (eventsNode != null && eventsNode.has("value")) {
            for (JsonNode event : eventsNode.get("value")) {
                try {
                    String eventId = event.get("id").asText();
                    String organizerEmail = event.get("organizer").get("emailAddress").get("address").asText();
                    String start = event.get("start").get("dateTime").asText();
                    String end = event.get("end").get("dateTime").asText();
                    String title = event.get("subject").asText();

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", eventId);
                    map.put("title", title);
                    map.put("start", start);
                    map.put("end", end);
                    map.put("email", organizerEmail);
                    merged.add(map);

                    addedIds.add(eventId);
                } catch (Exception e) {
                    System.err.println("❗ Błąd podczas przetwarzania wydarzenia: " + e.getMessage());
                }
            }
        }

        // 3. Dodaj lokalne tylko jeśli ich eventId == null (czyli nie zaakceptowane)
        for (Reservation r : localReservations) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", UUID.randomUUID().toString());  // fake ID żeby nie kolidowało z Outlookiem
            map.put("title", "Rezerwacja oczekująca");
            map.put("start", r.getStartMeeting().toString());
            map.put("end", r.getEndMeeting().toString());
            map.put("email", r.getOrganizerEmail());
            merged.add(map);
        }

        return merged;
    }

    public void createOutlookEventForUser(String accessToken, Reservation reservation, Hall hall) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
        String start = formatter.format(reservation.getStartMeeting());
        String end = formatter.format(reservation.getEndMeeting());

        Map<String, Object> event = Map.of(
                "subject", "Rezerwacja sali: " + hall.getName() + " [Osobiście]",
                "body", Map.of(
                        "contentType", "HTML",
                        "content", "Rezerwacja sali przez aplikację"
                ),
                "start", Map.of(
                        "dateTime", start,
                        "timeZone", "UTC"
                ),
                "end", Map.of(
                        "dateTime", end,
                        "timeZone", "UTC"
                ),
                "location", Map.of(
                        "displayName", hall.getName()
                ),
                "attendees", List.of(
                        Map.of(
                                "type", "resource",
                                "emailAddress", Map.of(
                                        "address", hall.getEmail(),
                                        "name", hall.getName()
                                )
                        )
                ),
                "isOnlineMeeting", false,
                "responseRequested", true
        );

        try {
            JsonNode response = webClient.post()
                    .uri("/me/events")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            // tylko jeśli sala zaakceptuje, ustawiamy ID wydarzenia
            if (response != null && response.has("id")) {
                String eventId = response.get("id").asText();
                String organizer = response.get("organizer").get("emailAddress").get("address").asText();

                reservation.setOutlookEventId(eventId);
                reservation.setOrganizerEmail(organizer);

                reservationRepository.save(reservation);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Nie udało się zapisać wydarzenia w Outlook: " + e.getMessage());
            // ale zostawiamy lokalnie
            reservation.setOutlookEventId(null);
            reservationRepository.save(reservation);
        }
    }




    public boolean deleteEventFromUserCalendar(String eventId, String accessToken) {
        try {
            webClient.delete()
                    .uri("/me/events/" + eventId)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException e) {
            return false;
        }
    }

    public boolean isReservedAtThisTime(Long hallId, Reservation reservation) {
        return !reservationRepository.findByHallIdAndStartMeetingBeforeAndEndMeetingAfter(
                hallId, reservation.getEndMeeting(), reservation.getStartMeeting()
        ).isEmpty();
    }

    public void createReservation(Hall hall, User user, Reservation reservation) {
        reservation.setHall(hall);
        reservation.setUser(user);
        reservation.setDate(Instant.now());
        reservationRepository.save(reservation);
    }

    public Optional<Reservation> findByOutlookEventId(String eventId) {
        return reservationRepository.findByOutlookEventId(eventId);
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
            if (!organizer.equalsIgnoreCase(userEmail)) {
                return false;
            }

            webClient.delete()
                    .uri("/users/" + hallEmail + "/events/" + eventId)
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return true;
        } catch (WebClientResponseException.NotFound nf) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
