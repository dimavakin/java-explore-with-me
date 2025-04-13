package ru.practicum.service.open;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;
    EventViewRepository eventViewRepository;

    @Override
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size) {

        final LocalDateTime filterStartDate = rangeStart != null ? rangeStart : LocalDateTime.now();
        sort = sort == null ? "EVENT_DATE" : sort.toUpperCase();

        if (rangeEnd != null && rangeEnd.isBefore(filterStartDate)) {
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
                .filter(e -> (!e.getEventDate().isBefore(filterStartDate)) &&
                        (rangeEnd == null || !e.getEventDate().isAfter(rangeEnd)))
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

}
