package com.example.BlogAPI.dto;

import com.example.BlogAPI.entities.Post;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommentaryRequest {
    @NotNull
    @Size(min = 6, max = 50, message = "Некорректный никнейм")
    private String author;

    @NotNull
    @Size(min = 5, max = 1000)
    private String text;

    @NotNull
    private Integer postId;
}
