package org.example.letstry.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private ZonedDateTime date = ZonedDateTime.now();
    private ZonedDateTime startMeeting;
    private ZonedDateTime endMeeting;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    // Getters and Setters
    public long getId() { return id; }
    public ZonedDateTime getDate() { return date; }
    public ZonedDateTime getStartMeeting() { return startMeeting; }
    public ZonedDateTime getEndMeeting() { return endMeeting; }
    public User getUser() { return user; }
    public Hall getHall() { return hall; }

    public void setId(long id) { this.id = id; }
    public void setDate(ZonedDateTime date) { this.date = date; }
    public void setStartMeeting(ZonedDateTime startMeeting) { this.startMeeting = startMeeting; }
    public void setEndMeeting(ZonedDateTime endMeeting) { this.endMeeting = endMeeting; }
    public void setUser(User user) { this.user = user; }
    public void setHall(Hall hall) { this.hall = hall; }
}
