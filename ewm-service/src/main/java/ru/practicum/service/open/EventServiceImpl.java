package ru.practicum.service.open;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Event;
import ru.practicum.mapper.EventMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.EventView;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.EventViewRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventViewRepository eventViewRepository;

    @Override
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid,
                                         String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = parseDateTime(rangeStart).orElse(now);
        LocalDateTime end = parseDateTime(rangeEnd).orElse(null);
        sort = sort == null ? "EVENT_DATE" : sort.toUpperCase();

        if (end != null && end.isBefore(start)) {
            throw new ValidationException("End date must be after start date");
        }

        Pageable pageable = createPageable(from, size, sort);

        List<Event> events = eventRepository.findPublicEvents(
                text != null ? text.trim().toLowerCase() : null,
                categories,
                paid,
                onlyAvailable,
                sort,
                pageable
        );

        List<Event> filteredEvents = events.stream()
                .filter(e -> (!e.getEventDate().isBefore(start)) &&
                        (end == null || !e.getEventDate().isAfter(end)))
                .toList();

        return filteredEvents.stream()
                .map(EventMapper::toEventShortDtoFromEvent).toList();
    }

    @Transactional
    @Override
    public EventFullDto getEvent(Long id, String ip) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Published event with id=%d not found", id)));

        boolean isUniqueIp = !eventViewRepository.existsByEventIdAndIp(id, ip);

        if (isUniqueIp) {
            eventRepository.incrementViews(id);

            EventView eventView = new EventView();
            eventView.setEventId(id);
            eventView.setIp(ip);
            eventViewRepository.save(eventView);
        }


        return EventMapper.toEventFullDtoFromEvent(event);
    }

    private Pageable createPageable(Integer from, Integer size, String sort) {
        if ("VIEWS".equalsIgnoreCase(sort)) {
            return PageRequest.of(from / size, size, Sort.by("views").descending());
        }
        return PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
    }


    private Optional<LocalDateTime> parseDateTime(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return Optional.empty();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return Optional.of(LocalDateTime.parse(dateTime, formatter));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format.");
        }
    }
}
