package com.example.BlogAPI.comment.dto;

import com.example.BlogAPI.post.dto.PostRequest;
import com.example.BlogAPI.user.dto.UserRequest;
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
    private Long id;
    private String text;
    private PostRequest post;
    private UserRequest user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
