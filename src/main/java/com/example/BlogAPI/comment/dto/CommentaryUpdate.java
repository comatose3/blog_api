package com.example.BlogAPI.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentaryUpdate {
    @NotBlank(message = "Text cannot be a blank")
    @Size(min = 1, max = 100, message = "Text must be between 1 and 100")
    private String text;
}
