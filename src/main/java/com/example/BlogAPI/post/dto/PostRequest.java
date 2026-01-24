package com.example.BlogAPI.post.dto;

import com.example.BlogAPI.user.dto.UserRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostRequest {

    @NotNull
    @Valid
    private UserRequest  user;

    @NotBlank
    @Size(min = 1, max = 50, message = "Некорректное название поста")
    private String name;

    @NotBlank
    @Size(min = 10, max = 1000, message = "Недопустимое количество символов")
    private String content;

    private List<String> tags;
}
