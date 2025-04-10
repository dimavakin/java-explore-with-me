package ru.practicum.service.personal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsFromUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with  userId=%d was not found", userId));
        }
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMapper::toParticipationRequestDtoFromRequest).toList();
    }

    @Transactional
    @Override
    public ParticipationRequestDto postRequestFromUser(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with  userId=%d was not found", userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with eventId=%d was not found", eventId)));

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new BadRequestException("Request already exists for user " + userId + " and event " + eventId);
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Cannot participate in unpublished event");
        }

        if (event.getRequestModeration()
                && event.getParticipantLimit() > 0
                && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new BadRequestException("Participant limit reached for event " + eventId);
        }

        Request request = new Request();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreatedOn(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            eventRepository.incrementConfirmedRequests(eventId, 1);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequestFromUser(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> new NotFoundException(
                String.format("Request with requestId=%d and userId=%d was not found", requestId, userId)));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDtoFromRequest(requestRepository.save(request));
    }
}
