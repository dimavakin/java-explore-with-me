package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.enums.EventAdminStateAction;
import ru.practicum.enums.EventState;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;


@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventFullDto> getEventsFromAdmin(List<Long> users, List<String> states,
                                                 List<Integer> categories, String rangeStart,
                                                 String rangeEnd, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);


        LocalDateTime start = (rangeStart != null && !rangeStart.isBlank()) ?
                parseDateTime(rangeStart) : null;

        LocalDateTime end = (rangeEnd != null && !rangeEnd.isBlank()) ?
                parseDateTime(rangeEnd) : null;
        Page<Event> events = eventRepository.findEventsWithFilters(
                (users == null || users.isEmpty()) ? null : users,
                (states == null || states.isEmpty()) ? null : parseStates(states),
                (categories == null || categories.isEmpty()) ? null : categories,
                pageable
        );
        List<Event> filteredEvents = events.stream()
                .filter(e -> (start == null || !e.getEventDate().isBefore(start)) &&
                        (end == null || !e.getEventDate().isAfter(end)))
                .toList();

        return filteredEvents.stream()
                .map(EventMapper::toEventFullDtoFromEvent)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        updateIfPresent(updateEventAdminRequest.getAnnotation(), event::setAnnotation);
        updateIfPresent(updateEventAdminRequest.getDescription(), event::setDescription);
        updateIfPresent(updateEventAdminRequest.getTitle(), event::setTitle);
        updateIfPresent(updateEventAdminRequest.isPaid(), event::setPaid);
        updateIfPresent(updateEventAdminRequest.getParticipantLimit(), event::setParticipantLimit);
        updateIfPresent(updateEventAdminRequest.isRequestModeration(), event::setRequestModeration);
        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationRepository.save(updateEventAdminRequest.getLocation());
            if (event.getLocation() != null) {
                locationRepository.deleteById(event.getLocation().getId());
            }
            event.setLocation(location);
        }

        updateEventDateIfPresent(updateEventAdminRequest.getEventDate(), event);
        processStateAction(updateEventAdminRequest.getStateAction(), event);
        updateCategoryIfPresent(updateEventAdminRequest.getCategory(), event);

        Event updatedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDtoFromEvent(updatedEvent);
    }

    private void updateCategoryIfPresent(Integer categoryId, Event event) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Category with id=%d was not found", categoryId)));
            event.setCategory(category);
        }
    }

    private List<EventState> parseStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return Collections.emptyList();
        }
        return states.stream()
                .map(state -> {
                    try {
                        return EventState.valueOf(state.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Invalid state: " + state);
                    }
                })
                .toList();
    }

    private void processStateAction(EventAdminStateAction stateAction, Event event) {
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    validatePublishAction(event);
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    validateRejectAction(event);
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new BadRequestException("Invalid state action");
            }
        }
    }

    private void validateRejectAction(Event event) {
        if (event.getState() == EventState.PUBLISHED) {
            throw new BadRequestException("Published events cannot be rejected");
        }
    }

    private void validatePublishAction(Event event) {
        if (event.getState() != EventState.PENDING) {
            throw new BadRequestException("Only pending events can be published");
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Event date must be at least 1 hour from now");
        }
    }

    private <T> void updateIfPresent(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    private void updateEventDateIfPresent(String eventDateStr, Event event) {
        if (eventDateStr != null) {
            LocalDateTime newEventDate = parseDateTime(eventDateStr);
            validateEventDate(newEventDate);
            event.setEventDate(newEventDate);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format");
        }
    }
}
