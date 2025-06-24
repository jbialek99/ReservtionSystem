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
            WebClient webClient,
            UserService userService
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

        List<Map<String, Object>> merged = new ArrayList<>();
        Set<String> addedEventIds = new HashSet<>();
        Set<String> addedTimeRanges = new HashSet<>();

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

                    addedEventIds.add(eventId);
                    addedTimeRanges.add(start + "|" + end);
                } catch (Exception e) {
                    System.err.println("❗ Błąd podczas przetwarzania wydarzenia: " + e.getMessage());
                }
            }
        }

        List<Reservation> allLocal = reservationRepository.findByHallId(hallId);

        for (Reservation r : allLocal) {
            String start = r.getStartMeeting().toString();
            String end = r.getEndMeeting().toString();
            String eventId = r.getOutlookEventId();
            String rangeKey = start + "|" + end;

            boolean alreadyInOutlook = eventId != null && addedEventIds.contains(eventId);
            boolean sameTimeInOutlook = addedTimeRanges.contains(rangeKey);
            if (alreadyInOutlook || sameTimeInOutlook) continue;

            String userName = Optional.ofNullable(r.getUser())
                    .map(user -> user.getFirstName() + " " + user.getLastName())
                    .orElse("Nieznany użytkownik");

            String rawTitle = Optional.ofNullable(r.getTitle()).orElse("");
            String finalTitle = (!rawTitle.startsWith(userName) && !rawTitle.isBlank())
                    ? userName + ": " + rawTitle
                    : (rawTitle.isBlank() ? userName : rawTitle);

            Map<String, Object> map = new HashMap<>();
            map.put("id", eventId != null ? eventId : UUID.randomUUID().toString());
            map.put("title", finalTitle);
            map.put("start", start);
            map.put("end", end);
            map.put("email", r.getOrganizerEmail());
            merged.add(map);
        }

        return merged;
    }


    public boolean createOutlookEventForUser(String accessToken, Reservation reservation, Hall hall) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
        String start = formatter.format(reservation.getStartMeeting());
        String end = formatter.format(reservation.getEndMeeting());

        String organizerName = reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName();
        String title = organizerName + ": " + reservation.getTitle();

        Map<String, Object> event = Map.of(
                "subject", title,
                "body", Map.of("contentType", "HTML", "content", "Rezerwacja sali przez aplikację"),
                "start", Map.of("dateTime", start, "timeZone", "UTC"),
                "end", Map.of("dateTime", end, "timeZone", "UTC"),
                "location", Map.of("displayName", hall.getName()),
                "attendees", List.of(Map.of(
                        "type", "resource",
                        "emailAddress", Map.of("address", hall.getEmail(), "name", hall.getName())
                )),
                "isOnlineMeeting", false,
                "responseRequested", true
        );

        try {
            System.out.println("📤 Wysyłam wydarzenie do Outlook:");
            System.out.println("➡️ Start: " + start);
            System.out.println("➡️ End: " + end);
            System.out.println("➡️ Sala: " + hall.getEmail());

            JsonNode response = webClient.post()
                    .uri("/me/events")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null) {
                System.out.println("✅ Odpowiedź z Outlooka OK. ID wydarzenia: " + response.path("id").asText());

                String organizer = response.path("organizer").path("emailAddress").path("address").asText();
                boolean accepted = false;

                for (JsonNode attendee : response.withArray("attendees")) {
                    String email = attendee.path("emailAddress").path("address").asText();
                    String responseStatus = attendee.path("status").path("response").asText();
                    System.out.println("🔍 Attendee: " + email + " | Status: " + responseStatus);

                    if (email.equalsIgnoreCase(hall.getEmail())) {
                        if ("accepted".equalsIgnoreCase(responseStatus)) {
                            accepted = true;
                            System.out.println("✅ Sala zaakceptowała.");
                        } else {
                            System.out.println("❌ Sala nie zaakceptowała. Status: " + responseStatus);
                        }
                    }
                }

                reservation.setTitle(title);
                reservation.setOrganizerEmail(organizer);
                reservation.setOutlookEventId(accepted ? response.path("id").asText() : null);
                reservationRepository.save(reservation);

                return accepted;
            } else {
                System.out.println("⚠️ Brak odpowiedzi z Outlooka.");
            }
        } catch (Exception e) {
            System.err.println("❗ Błąd podczas wysyłania wydarzenia do Outlooka: " + e.getMessage());
        }

        reservation.setTitle(title);
        reservation.setOutlookEventId(null);
        reservationRepository.save(reservation);
        return false;
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

    public void removeReservationFromDb(String eventId) {
        reservationRepository.deleteByOutlookEventId(eventId);
    }
}
