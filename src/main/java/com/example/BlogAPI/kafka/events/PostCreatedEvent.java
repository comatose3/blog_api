package com.example.BlogAPI.kafka.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostCreatedEvent extends BaseEvent {
    private Long postId;
    private Long userId;
    private String username;
    private String name;
    private String content;
    private List<String> tags;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public PostCreatedEvent(Long postId, Long userId, String username,
                            String name, String content, List<String> tags,
                            LocalDateTime createdAt) {
        super(UUID.randomUUID().toString(), LocalDateTime.now(),"POST_CREATED");
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.content = content;
        this.tags = tags;
        this.createdAt = createdAt;
    }

    public PostCreatedEvent(Long postId) {
        this.postId = postId;
    }
}
