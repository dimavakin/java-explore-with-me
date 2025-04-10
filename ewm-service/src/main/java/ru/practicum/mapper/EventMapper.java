package ru.practicum.mapper;

import ru.practicum.model.Category;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventMapper {
    public static EventFullDto toEventFullDtoFromEvent(Event event) {
        if (event == null) {
            throw new ValidationException("event can not be null");
        }
        EventFullDto dto = new EventFullDto();

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
        UserShortDto initiator = UserMapper.toUserShortDtoFromUser(event.getInitiator());

        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(categoryDto);
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setCreatedOn(formatDateTime(event.getCreatedOn()));
        dto.setDescription(event.getDescription());
        dto.setEventDate(formatDateTime(event.getEventDate()));
        dto.setInitiator(initiator);
        dto.setLocation(event.getLocation());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(formatDateTime(event.getPublishedOn()));
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());

        return dto;
    }

    public static EventShortDto toEventShortDtoFromEvent(Event event) {
        if (event == null) {
            throw new ValidationException("event can not be null");
        }
        EventShortDto dto = new EventShortDto();

        CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
        UserShortDto initiator = UserMapper.toUserShortDtoFromUser(event.getInitiator());

        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(categoryDto);
        dto.setDescription(event.getDescription());
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setCreatedOn(formatDateTime(event.getCreatedOn()));
        dto.setEventDate(formatDateTime(event.getEventDate()));
        dto.setInitiator(initiator);
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());

        return dto;
    }

    public static Event toEventFromNewEventDto(NewEventDto newEventDto, User user, Category category, Location location) {
        if (newEventDto == null) {
            throw new ValidationException("newEvent can not be null");
        }
        if (user == null) {
            throw new ValidationException("user can not be null");
        }
        if (category == null) {
            throw new ValidationException("category can not be null");
        }

        Event event = new Event();

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setInitiator(user);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(parseDateTime(newEventDto.getEventDate()));
        event.setLocation(location);
        event.setPaid(newEventDto.isPaid());
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }
        if (newEventDto.getParticipantLimit() != null) {
            event.setRequestModeration(newEventDto.isRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        event.setState(EventState.PENDING);
        event.setTitle(newEventDto.getTitle());
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0);
        event.setViews(0L);
        return event;
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format");
        }
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}


