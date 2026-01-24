package com.example.BlogAPI.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentUpdatedEvent extends BaseEvent {
    private Long commentId;
    private String text;
    private Long postId;
    private String postName;
    private Long userId;
    private String username;

    public CommentUpdatedEvent(Long commentId, String text, Long postId, String postName, Long userId, String username) {
        super(UUID.randomUUID().toString(), LocalDateTime.now(), "COMMENT_UPDATED");
        this.commentId = commentId;
        this.text = text;
        this.postId = postId;
        this.postName = postName;
        this.userId = userId;
        this.username = username;
    }
}
