package ru.practicum.service.open;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    EventRepository eventRepository;

    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d not found", eventId));
        }
        Page<Comment> comments = commentRepository.findByEventId(eventId, pageable);

        return comments.stream().map(CommentMapper::toCommentDtoFromComment).toList();
    }
}
