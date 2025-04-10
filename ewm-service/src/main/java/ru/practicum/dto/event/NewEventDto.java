package ru.practicum.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @Size(min = 20, max = 2000, message = "Annotation length must be between 20 and 2000 characters")
    @NotBlank
    @NotNull
    String annotation;
    @Positive(message = "Category ID must be positive")
    @NotNull
    Integer category;
    @Size(min = 20, max = 7000, message = "Description length must be between 20 and 7000 characters")
    @NotBlank
    @NotNull
    String description;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "Event date must be in format 'yyyy-MM-dd HH:mm:ss'")
    @NotNull
    String eventDate;
    @Valid
    @NotNull
    Location location;
    boolean paid;
    @Min(0)
    Integer participantLimit;
    boolean requestModeration;
    @Size(min = 3, max = 120, message = "Title length must be between 3 and 120 characters")
    @NotBlank
    @NotNull
    String title;
}
