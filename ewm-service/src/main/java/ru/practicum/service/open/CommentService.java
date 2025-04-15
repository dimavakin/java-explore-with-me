package ru.practicum.service.open;

import ru.practicum.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getComments(Long eventId, Integer from, Integer size);
}
