package com.example.BlogAPI.kafka.listeners;

import com.example.BlogAPI.kafka.events.PostCreatedEvent;
import com.example.BlogAPI.kafka.events.PostUpdatedEvent;
import com.example.BlogAPI.kafka.producer.KafkaProducerService;
import com.example.BlogAPI.post.Post;
import com.example.BlogAPI.post.PostsRepository;
import com.example.BlogAPI.tag.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PostEventListener {

    private final KafkaProducerService kafkaProducer;
    private final PostsRepository postsRepository;

    @Autowired
    public PostEventListener(KafkaProducerService kafkaProducer, PostsRepository postsRepository) {
        this.kafkaProducer = kafkaProducer;
        this.postsRepository = postsRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostCreated(PostCreatedEvent event) {

        Post post = postsRepository.findById(event.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + event.getPostId() + " not found"));

        PostCreatedEvent kafkaEvent = new PostCreatedEvent(
                post.getId(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getName(),
                post.getContent(),
                post.getTags() != null
                        ? post.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())
                        : List.of(),
                post.getCreatedAt()
        );

        log.info("TX committed -> sending POST_CREATED to Kafka: postId={}",
                event.getPostId());

        kafkaProducer.sendPostCreatedEvent(kafkaEvent);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostUpdated(PostUpdatedEvent event) {

        Post post = postsRepository.findById(event.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post with id: " + event.getPostId() + " not found"));

        PostUpdatedEvent kafkaEvent = new PostUpdatedEvent(
                post.getId(),
                post.getName(),
                post.getContent(),
                post.getTags() != null
                        ? post.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())
                        : List.of(),
                post.getUser().getId(),
                post.getUpdatedAt()
        );

        log.info("TX committed -> POST_UPDATED sent, postId={}", post.getId());
        kafkaProducer.sendPostUpdatedEvent(kafkaEvent);
    }
}
