package com.example.BlogAPI.kafka.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PostUpdatedEvent extends BaseEvent {
    private Long postId;
    private String name;
    private String content;
    private List<String> tags;
    private Long updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public PostUpdatedEvent(Long postId, String name, String content, List<String> tags, Long updatedBy, LocalDateTime updatedAt) {
        super(UUID.randomUUID().toString(), LocalDateTime.now(), "POST_UPDATED");
        this.postId = postId;
        this.name = name;
        this.content = content;
        this.tags = tags;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }
}
