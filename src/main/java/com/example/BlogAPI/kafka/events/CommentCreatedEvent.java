package com.example.BlogAPI.kafka.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentCreatedEvent extends BaseEvent {
    private Long commentId;
    private String text;
    private Long postId;
    private String postName;
    private Long userId;
    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public CommentCreatedEvent(String eventId, LocalDateTime timestamp, String eventType, Long commentId, String text, Long postId, String postName, Long userId, String username, LocalDateTime createdAt) {
        super(UUID.randomUUID().toString(), LocalDateTime.now(), "COMMENT_CREATED");
        this.commentId = commentId;
        this.text = text;
        this.postId = postId;
        this.postName = postName;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
    }
}
