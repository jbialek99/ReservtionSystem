package org.example.letstry.service;

import org.example.letstry.model.Reservation;
import org.example.letstry.model.ReservationStatus;
import org.example.letstry.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class GraphApiService {

    private static final Logger logger = LoggerFactory.getLogger(GraphApiService.class);

    private final WebClient graphClient;
    private final ReservationRepository reservationRepository;

    private final DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public GraphApiService(WebClient graphClient, ReservationRepository reservationRepository) {
        this.graphClient = graphClient;
        this.reservationRepository = reservationRepository;
    }

    public void sendReservationRequestToOutlook(Reservation reservation) {
        String accessToken = getAccessToken();

        // Konwersja dat do ISO8601, zakładając, że getStartMeeting i getEndMeeting zwracają Instant
        String startTime = isoFormatter.format(reservation.getStartMeeting().atZone(ZoneId.of("Europe/Warsaw")).toLocalDateTime());
        String endTime = isoFormatter.format(reservation.getEndMeeting().atZone(ZoneId.of("Europe/Warsaw")).toLocalDateTime());

        Map<String, Object> eventPayload = Map.of(
                "subject", reservation.getTitle(),
                "body", Map.of(
                        "contentType", "HTML",
                        "content", reservation.getDescription() != null ? reservation.getDescription() : ""
                ),
                "start", Map.of(
                        "dateTime", startTime,
                        "timeZone", "Europe/Warsaw"
                ),
                "end", Map.of(
                        "dateTime", endTime,
                        "timeZone", "Europe/Warsaw"
                ),
                "location", Map.of(
                        "displayName", reservation.getHall() != null ? reservation.getHall().getName() : "Sala konferencyjna"
                ),
                "attendees", List.of(
                        Map.of(
                                "emailAddress", Map.of(
                                        "address", reservation.getHall() != null ? reservation.getHall().getEmail() : "",
                                        "name", reservation.getHall() != null ? reservation.getHall().getName() : "Sala konferencyjna"
                                ),
                                "type", "required"
                        )
                )
        );

        try {
            Map response = graphClient.post()
                    .uri("https://graph.microsoft.com/v1.0/users/{userId}/events", reservation.getOrganizerEmail())
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .bodyValue(eventPayload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                String eventId = (String) response.get("id");
                List<Map<String, Object>> attendees = (List<Map<String, Object>>) response.get("attendees");

                String responseStatus = "pending";
                if (attendees != null && !attendees.isEmpty()) {
                    Map<String, Object> firstAttendee = attendees.get(0);
                    Map<String, Object> status = (Map<String, Object>) firstAttendee.get("status");
                    if (status != null && status.get("response") instanceof String) {
                        responseStatus = (String) status.get("response");
                    }
                }

                ReservationStatus reservationStatus;
                switch (responseStatus.toLowerCase()) {
                    case "confirmed":
                        reservationStatus = ReservationStatus.CONFIRMED;
                        break;
                    case "rejected":
                        reservationStatus = ReservationStatus.REJECTED;
                        break;
                    default:
                        reservationStatus = ReservationStatus.PENDING;
                }

                updateReservation(reservation.getId(), reservationStatus, eventId);
            } else {
                logger.error("Brak odpowiedzi z Microsoft Graph API przy tworzeniu wydarzenia");
            }
        } catch (Exception e) {
            logger.error("Błąd podczas tworzenia wydarzenia w Outlook: {}", e.getMessage(), e);
        }
    }

    private void updateReservation(Long id, ReservationStatus status, String eventId) {
        reservationRepository.findById(id).ifPresent(reservation -> {
            reservation.setStatus(status);
            reservation.setOutlookEventId(eventId);
            reservationRepository.save(reservation);
        });
    }

    private String getAccessToken() {
        // TODO: Implementacja pozyskania tokenu OAuth2 (np. client_credentials)
        return "DYNAMIC_TOKEN";
    }
}
