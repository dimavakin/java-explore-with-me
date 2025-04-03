package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.stats.StatsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<HitEntity, Long> {
    @Query("SELECT NEW ru.practicum.dto.stats.StatsResponse(h.app, h.uri, COUNT(h.id)) " +
            "FROM HitEntity h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.id) DESC")
    List<StatsResponse> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT NEW ru.practicum.dto.stats.StatsResponse(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM HitEntity h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri")
    List<StatsResponse> getUniqueStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}
