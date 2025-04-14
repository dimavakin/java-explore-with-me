package ru.practicum.controller.open;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.hit.HitRequest;
import ru.practicum.service.open.CommentService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    StatsClient statsClient;
    static String APP_NAME = "ewm-main-service";
    CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getComments(@PathVariable @Positive Long eventId, HttpServletRequest request, @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        HitRequest hitRequest = new HitRequest(APP_NAME, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        statsClient.saveHit(hitRequest);

        return commentService.getComments(eventId,from,size);
    }
}
