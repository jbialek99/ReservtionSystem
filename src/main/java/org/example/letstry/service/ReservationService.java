package org.example.letstry.service;

import org.example.letstry.model.Hall;
import org.example.letstry.model.Reservation;
import org.example.letstry.model.User;
import org.example.letstry.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Map<String, Object>> getReservationByHall(Long hallId) {
        List<Reservation> reservations = reservationRepository.findByHallId(hallId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", "Rezerwacja");
            event.put("start", reservation.getStartMeeting().toString());
            event.put("end", reservation.getEndMeeting().toString());
            event.put("id", reservation.getId());
            result.add(event);
        }
        return result;
    }

    public boolean isReservedAtThisTime(Long hallId, Reservation reservation) {
        List<Reservation> reservations = reservationRepository.findByHallIdAndStartMeetingBeforeAndEndMeetingAfter(
                hallId,
                reservation.getEndMeeting(),
                reservation.getStartMeeting()
        );
        return !reservations.isEmpty();
    }

    public void createReservation(Hall hall, User user, Reservation reservation) {
        reservation.setHall(hall);
        reservation.setUser(user);
        reservation.setDate(ZonedDateTime.now());
        reservationRepository.save(reservation);
    }

    public void updateReservation(Reservation updatedReservation, Reservation existingReservation) {
        existingReservation.setDate(updatedReservation.getDate());
        existingReservation.setStartMeeting(updatedReservation.getStartMeeting());
        existingReservation.setEndMeeting(updatedReservation.getEndMeeting());
        reservationRepository.save(existingReservation);
    }

    public Optional<Reservation> findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}
