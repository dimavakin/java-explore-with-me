package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation", columnDefinition = "TEXT")
    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @ToString.Exclude
    User initiator;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "location_id")
    @ToString.Exclude
    Location location;

    @Column
    boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "request_moderation")
    boolean requestModeration;

    @Column
    @Enumerated(EnumType.STRING)
    EventState state;

    @Column
    String title;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column
    Long views;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;
}
