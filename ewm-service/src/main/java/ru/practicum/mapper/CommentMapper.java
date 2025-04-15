package ru.practicum.mapper;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentMapper {
    public static CommentDto toCommentDtoFromComment(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setUser(UserMapper.toUserDtoFromUser(comment.getUser()));
        dto.setCreated(formatDateTime(comment.getCreate()));
        dto.setEventId(comment.getEvent().getId());

        if (comment.getParentComment() != null) {
            CommentDto parentDto = new CommentDto();
            parentDto.setId(comment.getParentComment().getId());
            parentDto.setText(comment.getParentComment().getText());
            parentDto.setUser(UserMapper.toUserDtoFromUser(comment.getParentComment().getUser()));
            parentDto.setCreated(formatDateTime(comment.getParentComment().getCreate()));
            parentDto.setEventId(comment.getParentComment().getEvent().getId());
            dto.setParentComment(parentDto);
        }

        return dto;
    }

    public static Comment toCommentFromNewCommentDto(NewCommentDto newCommentDto, Event event, User user, Comment parentComment) {
        if (newCommentDto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setText(newCommentDto.getText());
        comment.setEvent(event);
        comment.setCreate(LocalDateTime.now());
        comment.setParentComment(parentComment);

        return comment;
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
