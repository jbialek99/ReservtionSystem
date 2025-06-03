package org.example.letstry.repository;

import org.example.letstry.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByHallId(Long hallId);
    List<Reservation> findByHallIdAndStartMeetingBeforeAndEndMeetingAfter(Long hallId, Instant end, Instant start);

}
