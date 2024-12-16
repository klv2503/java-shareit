package ru.practicum.shareit.item.dto.comments;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentInputDto {

    private String text;

    private Long item;

    private Long authorName;

}
