package com.example.BlogAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentaryResponse {
    private Integer id;
    private String author;
    private String text;
    private Integer postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
