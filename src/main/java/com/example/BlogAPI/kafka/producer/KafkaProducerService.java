package com.example.BlogAPI.kafka.producer;

import com.example.BlogAPI.kafka.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.post-created}")
    private String postCreatedTopic;

    @Value("${kafka.topics.post-updated}")
    private String postUpdatedTopic;

    @Value("${kafka.topics.comment-created}")
    private String commentCreatedTopic;

    @Value("comment-updated-topic")
    private String commentUpdatedTopic;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    public void sendPostCreatedEvent(PostCreatedEvent event) {
        sendEvent(postCreatedTopic, event.getPostId().toString(), event);
    }

    public void sendPostUpdatedEvent(PostUpdatedEvent event) {
        sendEvent(postUpdatedTopic, event.getPostId().toString(), event);
    }

    public void sendCommentCreatedEvent(CommentCreatedEvent event) {
        sendEvent(commentCreatedTopic, event.getCommentId().toString(), event);
    }

    public void sendCommentUpdatedEvent(CommentUpdatedEvent event) {
        sendEvent(commentUpdatedTopic, event.getCommentId().toString(), event);
    }

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        sendEvent(userRegisteredTopic, event.getUserId().toString(), event);
    }

    private void sendEvent(String topic, String key, Object event) {
        log.info("Sending event to topic {}: {}", topic, event);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent event to topic {} with offset {}",
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event to topic {}: {}", topic, ex.getMessage());
            }
        });
    }
}
