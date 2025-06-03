package org.example.letstry.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Instant date = Instant.now();

    private Instant startMeeting;
    private Instant endMeeting;
    private String outlookEventId;
    private String organizerEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    // Getters and setters

    public long getId() {
        return id;
    }
    public Instant getDate() {
        return date;
    }
    public Instant getStartMeeting() {
        return startMeeting;
    }
    public Instant getEndMeeting() {
        return endMeeting;
    }
    public User getUser() {
        return user;
    }
    public Hall getHall() {
        return hall;
    }
    public String getOutlookEventId() {
        return outlookEventId;
    }
    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setDate(Instant date) {
        this.date = date;
    }
    public void setStartMeeting(Instant startMeeting) {
        this.startMeeting = startMeeting;
    }
    public void setEndMeeting(Instant endMeeting) {
        this.endMeeting = endMeeting;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setHall(Hall hall) {
        this.hall = hall;
    }
    public void setOutlookEventId(String outlookEventId) {
        this.outlookEventId = outlookEventId;
    }
    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }
}
