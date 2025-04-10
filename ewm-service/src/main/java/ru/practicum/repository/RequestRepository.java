package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Request;
import ru.practicum.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByEventId(Long eventId);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);

    List<Request> findAllByRequesterId(Long requesterId);

    @Query("SELECT r FROM Request r WHERE r.id IN :ids AND r.status = :status")
    List<Request> findAllByIdInAndStatus(
            @Param("ids") List<Long> ids,
            @Param("status") RequestStatus status
    );

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
}
