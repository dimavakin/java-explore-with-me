package ru.practicum.controller.personal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.personal.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final PrivateRequestService privateRequestService;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsFromUser(@PathVariable(name = "userId") Long userId) {
        return privateRequestService.getRequestsFromUser(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequestFromUser(@PathVariable(name = "userId") Long userId, @RequestParam Long eventId) {
        return privateRequestService.postRequestFromUser(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestFromUser(@PathVariable(name = "userId") Long userId, @PathVariable Long requestId) {
        return privateRequestService.cancelRequestFromUser(userId, requestId);
    }
}