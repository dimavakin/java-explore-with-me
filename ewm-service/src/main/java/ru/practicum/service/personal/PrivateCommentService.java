package ru.practicum.service.personal;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

public interface PrivateCommentService {
    CommentDto getComment(Long userId, Long commentId);

    CommentDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    void deleteComment(Long userId, Long commentId);

    CommentDto patchComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto);
}
