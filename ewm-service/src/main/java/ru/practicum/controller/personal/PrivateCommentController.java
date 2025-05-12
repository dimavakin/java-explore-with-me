package ru.practicum.controller.personal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.service.personal.PrivateCommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateCommentController {
    PrivateCommentService privateCommentService;

    @GetMapping("/{userId}/comments/{commentId}")
    public CommentDto getComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        return privateCommentService.getComment(userId, commentId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events/{eventId}/comments")
    public CommentDto postComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId, @RequestBody @Valid NewCommentDto newCommentDto) {
        return privateCommentService.postComment(userId, eventId, newCommentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId) {
        privateCommentService.deleteComment(userId, commentId);
        return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto patchComment(@PathVariable @Positive Long userId, @PathVariable @Positive Long commentId, @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        return privateCommentService.patchComment(userId, commentId, updateCommentDto);
    }
}
