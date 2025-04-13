package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EventView;

public interface EventViewRepository extends JpaRepository<EventView, Long> {
    boolean existsByEventIdAndIp(Long eventId, String ip);
}