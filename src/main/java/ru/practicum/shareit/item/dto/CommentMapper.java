package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Component
public class CommentMapper {

    public static CommentDto mapCommentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(),
                comment.getItem().getName(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> mapCommentListToCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapCommentToCommentDto)
                .toList();
    }

}