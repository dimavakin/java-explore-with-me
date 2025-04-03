package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.hit.HitRequest;
import ru.practicum.dto.stats.StatsResponse;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final HitsRepository hitsRepository;

    public List<StatsResponse> getStats(LocalDateTime start, LocalDateTime end,
                                        List<String> uris, boolean unique) {
        if (unique) {
            return getUniqueStats(start, end, uris);
        } else {
            return getAllStats(start, end, uris);
        }
    }

    private List<StatsResponse> getAllStats(LocalDateTime start, LocalDateTime end,
                                            List<String> uris) {
        return hitsRepository.getStats(start, end, uris);
    }

    private List<StatsResponse> getUniqueStats(LocalDateTime start, LocalDateTime end,
                                               List<String> uris) {
        return hitsRepository.getUniqueStats(start, end, uris);
    }

    @Override
    public void saveHit(HitRequest request) {
        validateHitRequest(request);

        hitsRepository.save(HitsMapper.toEntity(request));
    }

    private void validateHitRequest(HitRequest request) {
        if (request.getApp() == null || request.getApp().isBlank()) {
            throw new ValidationException("App name cannot be empty");
        }
        if (request.getUri() == null || request.getUri().isBlank()) {
            throw new ValidationException("URI cannot be empty");
        }
        if (request.getIp() == null || request.getIp().isBlank()) {
            throw new ValidationException("IP cannot be empty");
        }
        if (request.getTimestamp() == null) {
            throw new ValidationException("Timestamp cannot be null");
        }
    }
}
