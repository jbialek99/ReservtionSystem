package org.example.letstry.model;

import jakarta.persistence.*;

import java.time.Instant;


@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Instant date = Instant.now();
    private String title;
    private Instant startMeeting;
    private Instant endMeeting;
    private String outlookEventId;
    private String organizerEmail;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    private Hall hall;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

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

    public String getTitle() {
        return title;
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
    public void setTitle(String title) {
        this.title = title;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
