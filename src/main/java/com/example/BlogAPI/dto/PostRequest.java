package com.example.BlogAPI.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    @NotBlank
    @Size(min = 6, max = 50, message = "Некорректный никнейм")
    private String author;

    @NotBlank
    @Size(min = 5, max = 50, message = "Некорректное название поста")
    private String name;

    @NotBlank
    @Size(min = 10, max = 1000, message = "Недопустимое количество символов")
    private String content;
}
