package com.example.BlogAPI.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentaryRequest {
    @NotNull
    @Size(min = 2, max = 50, message = "Некорректный никнейм")
    private String author;

    @NotNull
    @Size(min = 1, max = 1000)
    private String text;
}
