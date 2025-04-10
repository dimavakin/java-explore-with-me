package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(ValidationException e) {
        log.error("Validation error", e);
        return new ApiError(
                "Incorrectly made request.",
                e.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.error("Не найдено: ", e);
        return new ApiError(
                "The required object was not found.",
                e.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleEventUpdate(final EventUpdateException e) {
        log.error("Ошибка в обновлении: ", e);
        return new ApiError(
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                HttpStatus.FORBIDDEN.name()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDuplicated(final DuplicatedDataException e) {
        log.error("Дублирование данных: ", e);
        return new ApiError(
                "Integrity constraint has been violated.",
                e.getMessage(),
                HttpStatus.CONFLICT.name()
        );
    }
}
