package com.example.BlogAPI.tag.dto;

import com.example.BlogAPI.post.dto.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private Long id;
    private String name;
    private List<PostResponse> postResponses;
    private LocalDateTime createdAt;
}
