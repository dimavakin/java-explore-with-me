package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Event;
import ru.practicum.enums.EventState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR " +
            "   (e.annotation ILIKE %:text% OR " +
            "    e.description ILIKE %:text%)) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:onlyAvailable = false OR " +
            "   e.participantLimit = 0 OR " +
            "   e.participantLimit > (" +
            "       SELECT COUNT(r) FROM Request r WHERE r.event.id = e.id AND r.status = 'CONFIRMED'" +
            "   )) " +
            "ORDER BY " +
            "   CASE WHEN :sort = 'EVENT_DATE' THEN e.eventDate END DESC, " +
            "   CASE WHEN :sort = 'VIEWS' THEN e.views END DESC")
    List<Event> findPublicEvents(
            @Param("text") String text,
            @Param("categories") List<Integer> categories,
            @Param("paid") Boolean paid,
            @Param("onlyAvailable") Boolean onlyAvailable,
            @Param("sort") String sort,
            Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            WHERE (:users IS NULL OR e.initiator.id IN :users)
              AND (:states IS NULL OR e.state IN :states)
              AND (:categories IS NULL OR e.category.id IN :categories)
            """)
    Page<Event> findEventsWithFilters(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Integer> categories,
            Pageable pageable
    );

    Optional<Event> findByIdAndState(Long id, EventState state);

    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    boolean existsByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Modifying
    @Query("UPDATE Event e SET e.views = e.views + 1 WHERE e.id = :eventId")
    void incrementViews(@Param("eventId") Long eventId);

    @Modifying
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests + :increment WHERE e.id = :eventId")
    void incrementConfirmedRequests(@Param("eventId") Long eventId, @Param("increment") int increment);

    boolean existsByCategoryId(Integer categoryId);
}
