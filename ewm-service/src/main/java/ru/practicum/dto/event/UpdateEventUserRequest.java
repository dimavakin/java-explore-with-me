package ru.practicum.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.EventUserStateAction;
import ru.practicum.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Annotation length must be between 20 and 2000 characters")
    String annotation;
    @Positive(message = "Category ID must be positive")
    Integer category;
    @Size(min = 20, max = 7000, message = "Description length must be between 20 and 7000 characters")
    String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Event date must be in format 'yyyy-MM-dd HH:mm:ss'")
    String eventDate;
    @Valid
    Location location;
    Boolean paid;
    @PositiveOrZero(message = "Participant limit must be positive or zero")
    Integer participantLimit;
    Boolean requestModeration;
    EventUserStateAction stateAction;
    @Size(min = 3, max = 120, message = "Title length must be between 3 and 120 characters")
    String title;
}
