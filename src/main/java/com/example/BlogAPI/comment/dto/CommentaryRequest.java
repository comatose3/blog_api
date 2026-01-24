package com.example.BlogAPI.comment.dto;

import com.example.BlogAPI.user.dto.UserRequest;
import jakarta.validation.Valid;
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
    @Valid
    private UserRequest user;

    @NotNull
    @Size(min = 1, max = 1000)
    private String text;
}
