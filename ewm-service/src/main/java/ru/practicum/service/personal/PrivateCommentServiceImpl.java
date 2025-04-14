package ru.practicum.service.personal;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    EventRepository eventRepository;

    @Override
    public CommentDto getComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d not found", commentId)));
        if (!comment.getUser().getId().equals(userId)) {
            throw new ValidationException("user not author");
        }
        return CommentMapper.toCommentDtoFromComment(comment);
    }

    @Override
    public CommentDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d not found", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Event is not published");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));
        Comment parentComment = null;
        if (newCommentDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(newCommentDto.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found"));
        }
        Comment comment = commentRepository.save(CommentMapper.toCommentFromNewCommentDto(newCommentDto, event, user, parentComment));

        return CommentMapper.toCommentDtoFromComment(comment);
    }

    @Override
    public ResponseEntity<Void> deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d not found", commentId)));
        if (comment.getUser().getId().equals(userId) || comment.getEvent().getInitiator().getId().equals(userId)) {
            commentRepository.deleteById(commentId);
            return ResponseEntity.status(204).build();
        } else {
            throw new BadRequestException("The user is not the author or the initiator");
        }
    }

    @Override
    public CommentDto patchComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%d not found", commentId)));
        if (!comment.getUser().getId().equals(userId)) {
            throw new BadRequestException("The user is not the author");
        }
        comment.setText(updateCommentDto.getText());

        return CommentMapper.toCommentDtoFromComment(commentRepository.save(comment));
    }
}
