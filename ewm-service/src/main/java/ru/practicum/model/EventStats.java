package ru.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_stats")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventStats {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long eventId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_id")
    Event event;

    @Column
    Long views;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    public EventStats(Event event, Long views, Integer confirmedRequests) {
        this.event = event;
        this.views = views;
        this.confirmedRequests = confirmedRequests;
    }
}
