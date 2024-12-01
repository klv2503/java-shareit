package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.util.Comparator;
import java.util.List;

@Component
public class ShortCommentMapper {

    public static ShortCommentDto mapCommentToShortComment(Comment comment) {
        return
            (comment == null) ? null : new ShortCommentDto(comment.getId(), comment.getText(),
                    comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<ShortCommentDto> mapCommentListToShortCommentList(List<Comment> comments) {
        if (comments.isEmpty())
            return List.of();
        return comments.stream()
                .map(ShortCommentMapper::mapCommentToShortComment)
                .sorted(Comparator.comparing(ShortCommentDto::getCreated).reversed())
                .toList();
    }
}
