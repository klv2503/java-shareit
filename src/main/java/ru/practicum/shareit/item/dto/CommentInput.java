package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommentInput {

    @NotBlank
    @Size(max = 200)
    private String text;

    private Long item;

    private Long authorName;

}
