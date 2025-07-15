package org.example.letstry.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.letstry.model.Hall;
import org.example.letstry.model.Reservation;
import org.example.letstry.model.ReservationStatus;
import org.example.letstry.model.User;
import org.example.letstry.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

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

        String hallEmail = hall.getEmail();
        String accessToken = graphTokenService.getAppAccessToken();
        List<Map<String, Object>> merged = new ArrayList<>();

        try {
            JsonNode eventsNode = webClient.get()
                    .uri("/users/" + hallEmail + "/calendar/events")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (eventsNode != null && eventsNode.has("value")) {
                for (JsonNode event : eventsNode.get("value")) {
                    try {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", event.get("id").asText());
                        map.put("title", event.get("subject").asText());
                        map.put("start", event.get("start").get("dateTime").asText());
                        map.put("end", event.get("end").get("dateTime").asText());
                        map.put("email", event.get("organizer").get("emailAddress").get("address").asText());
                        merged.add(map);
                    } catch (Exception e) {
                        logger.warn("Błąd przetwarzania pojedynczego wydarzenia: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Błąd podczas pobierania wydarzeń z Outlooka: {}", e.getMessage());
        }

        return merged;
    }

    public boolean createOutlookEventForUser(String accessToken, Reservation reservation, Hall hall) {
        String start = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(reservation.getStartMeeting());
        String end = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC).format(reservation.getEndMeeting());

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
            JsonNode response = webClient.post()
                    .uri("/me/events")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (response != null) {
                String organizer = response.path("organizer").path("emailAddress").path("address").asText();
                String eventId = response.path("id").asText();
                boolean confirmed = false;

                for (JsonNode attendee : response.withArray("attendees")) {
                    String email = attendee.path("emailAddress").path("address").asText();
                    String responseStatus = attendee.path("status").path("response").asText();

                    if (email.equalsIgnoreCase(hall.getEmail()) && "confirmed".equalsIgnoreCase(responseStatus)) {
                        confirmed = true;
                        break;
                    }
                }

                reservation.setTitle(title);
                reservation.setOrganizerEmail(organizer);
                if (confirmed) {
                    reservation.setOutlookEventId(eventId);
                    reservation.setStatus(ReservationStatus.CONFIRMED);
                } else {
                    reservation.setOutlookEventId(null);
                    reservation.setStatus(ReservationStatus.PENDING);
                }
                reservationRepository.save(reservation);

                return confirmed;
            }
        } catch (Exception e) {
            logger.error("Błąd przy tworzeniu wydarzenia w Outlooku: {}", e.getMessage());
        }

        reservation.setTitle(title);
        reservation.setOutlookEventId(null);
        reservation.setStatus(ReservationStatus.PENDING);
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
            logger.error("Błąd przy próbie usunięcia wydarzenia: {}", e.getMessage());
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
        reservation.setStatus(ReservationStatus.PENDING);  // ustawiamy status na PENDING przy tworzeniu
        reservationRepository.save(reservation);
    }

    public Optional<Reservation> findByOutlookEventId(String eventId) {
        return reservationRepository.findByOutlookEventId(eventId);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public void removeReservationFromDb(String eventId) {
        reservationRepository.deleteByOutlookEventId(eventId);
    }

    public boolean isEventConfirmed(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .orElse(false);
    }
}
