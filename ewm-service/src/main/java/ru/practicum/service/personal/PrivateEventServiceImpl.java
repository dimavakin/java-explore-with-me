package ru.practicum.service.personal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.model.Event;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.enums.EventState;
import ru.practicum.enums.EventUserStateAction;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.model.Request;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.repository.RequestRepository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId) || from < 0 || size <= 0) {
            return List.of();
        }

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        return events.stream().map(EventMapper::toEventShortDtoFromEvent).toList();
    }

    @Transactional
    @Override
    public EventFullDto postEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getCategory() == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }

        validateEventDate(newEventDto.getEventDate());
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with userId=%d was not found", userId)));

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new NotFoundException(
                String.format("category with categoryId=%d was not found", newEventDto.getCategory())));

        Location location = locationRepository.save(newEventDto.getLocation());

        Event event = EventMapper.toEventFromNewEventDto(newEventDto, user, category, location);

        return EventMapper.toEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d for user id=%d not found", eventId, userId)));

        return EventMapper.toEventFullDtoFromEvent(event);
    }

    @Transactional
    @Override
    public EventFullDto patchEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d for user id=%d not found", eventId, userId)));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("The event has already been published");
        }
        updateIfPresent(updateEventUserRequest.getAnnotation(), event::setAnnotation);
        updateIfPresent(updateEventUserRequest.getDescription(), event::setDescription);
        updateIfPresent(updateEventUserRequest.getTitle(), event::setTitle);
        updateIfPresent(updateEventUserRequest.getPaid(), event::setPaid);
        updateIfPresent(updateEventUserRequest.getParticipantLimit(), event::setParticipantLimit);
        updateIfPresent(updateEventUserRequest.getRequestModeration(), event::setRequestModeration);

        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationRepository.save(updateEventUserRequest.getLocation());
            if (event.getLocation() != null) {
                locationRepository.deleteById(event.getLocation().getId());
            }
            event.setLocation(location);
        }

        updateEventDateIfPresent(updateEventUserRequest.getEventDate(), event);
        processStateAction(event, updateEventUserRequest.getStateAction());
        updateCategoryIfPresent(updateEventUserRequest.getCategory(), event);

        Event updatedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDtoFromEvent(updatedEvent);
    }

    private void updateEventDateIfPresent(LocalDateTime eventDate, Event event) {
        if (eventDate != null) {
            validateEventDate(eventDate);
            event.setEventDate(eventDate);
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("Событие не найдено или не принадлежит пользователю");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequest)
                .toList();
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchRequests(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found or access denied"));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new BadRequestException("No need to confirm requests for this event");
        }

        List<Request> requests = requestRepository.findAllByIdInAndStatus(
                updateRequest.getRequestIds(),
                RequestStatus.PENDING
        );

        if (requests.isEmpty()) {
            throw new BadRequestException("No pending requests found");
        }

        return updateRequest.getStatus() == RequestStatus.CONFIRMED
                ? processConfirmation(event, requests)
                : processRejection(requests);
    }

    private EventRequestStatusUpdateResult processRejection(List<Request> requests) {
        requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        List<Request> rejected = requestRepository.saveAll(requests);

        return new EventRequestStatusUpdateResult(
                Collections.emptyList(),
                rejected.stream().map(RequestMapper::toParticipationRequestDtoFromRequest).collect(Collectors.toList())
        );
    }


    private EventRequestStatusUpdateResult processConfirmation(Event event, List<Request> requests) {
        int availableSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (availableSlots <= 0) {
            throw new BadRequestException("The participant limit has been reached");
        }

        List<Request> toConfirm = requests.stream()
                .limit(availableSlots)
                .peek(r -> r.setStatus(RequestStatus.CONFIRMED))
                .collect(Collectors.toList());

        List<Request> toReject = requests.stream()
                .skip(availableSlots)
                .peek(r -> r.setStatus(RequestStatus.REJECTED))
                .collect(Collectors.toList());

        requestRepository.saveAll(toConfirm);
        requestRepository.saveAll(toReject);

        eventRepository.incrementConfirmedRequests(event.getId(), toConfirm.size());

        return new EventRequestStatusUpdateResult(
                toConfirm.stream().map(RequestMapper::toParticipationRequestDtoFromRequest).collect(Collectors.toList()),
                toReject.stream().map(RequestMapper::toParticipationRequestDtoFromRequest).collect(Collectors.toList())
        );
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Event date must be at least 2 hour from now");
        }
    }

    private void updateCategoryIfPresent(Integer categoryId, Event event) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Category with id=%d was not found", categoryId)));
            event.setCategory(category);
        }
    }

    private <T> void updateIfPresent(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    private void processStateAction(Event event, EventUserStateAction eventUserStateAction) {
        if (eventUserStateAction != null) {
            switch (eventUserStateAction) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new BadRequestException("Invalid state action: " + eventUserStateAction.toString());
            }
        }

    }

}
