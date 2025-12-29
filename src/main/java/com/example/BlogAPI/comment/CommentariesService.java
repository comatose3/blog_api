package com.example.BlogAPI.comment;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.comment.dto.CommentaryResponse;
import com.example.BlogAPI.comment.dto.CommentaryUpdate;
import com.example.BlogAPI.kafka.events.CommentCreatedEvent;
import com.example.BlogAPI.kafka.producer.KafkaProducerService;
import com.example.BlogAPI.post.Post;
import com.example.BlogAPI.post.PostsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class CommentariesService {
    private final CommentariesRepository commentariesRepository;
    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public CommentariesService(CommentariesRepository commentariesRepository, PostsRepository postsRepository, ModelMapper modelMapper, KafkaProducerService kafkaProducerService) {
        this.commentariesRepository = commentariesRepository;
        this.postsRepository = postsRepository;
        this.modelMapper = modelMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    public List<CommentaryResponse> getAllCommentariesByPost(Long postId) {
        return commentariesRepository.findAll().stream()
                .map(commentary -> convertToCommentaryResponse(commentary))
                .collect(Collectors.toList());
    }

    public CommentaryResponse getCommentaryById(Long commentaryId) {
        Commentary commentary = commentariesRepository.findById(commentaryId)
                .orElseThrow(() -> new EntityNotFoundException("Commentary with id: " + commentaryId + " not found"));

        return convertToCommentaryResponse(commentary);
    }

    @Transactional
    public CommentaryResponse writeCommentary(Long postId, CommentaryRequest commentaryRequest) {
        log.info("Creating comment for post: {}", postId);

        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id: " + postId + " not found"));

        Commentary commentary = convertToCommentary(commentaryRequest);
        commentary.setPost(post);

        Commentary savedCommentary = commentariesRepository.save(commentary);

        try {
            CommentCreatedEvent event = new CommentCreatedEvent(
                    savedCommentary.getId(),
                    savedCommentary.getText(),
                    post.getId(),
                    post.getName(),
                    savedCommentary.getUser().getId(),
                    savedCommentary.getUser().getUsername(),
                    savedCommentary.getCreatedAt()
            );

            kafkaProducerService.sendCommentCreatedEvent(event);
            log.info("Comment created and event sent to Kafka: commentId={}", savedCommentary.getId());
        } catch (Exception e) {
            log.error("Failed to send CommentCreatedEvent to Kakfa: {}", e.getMessage());
        }

        return convertToCommentaryResponse(savedCommentary);
    }

    @Transactional
    public CommentaryResponse updateCommentary(Long commentaryId, CommentaryUpdate commentaryUpdate) {
        Commentary commentaryToUpdate = commentariesRepository.findById(commentaryId)
                .orElseThrow(() -> new EntityNotFoundException("Commentary with id: " + commentaryId + " not found"));

        commentaryToUpdate.setText(commentaryUpdate.getText());

        return convertToCommentaryResponse(commentariesRepository.save(commentaryToUpdate));
    }

    @Transactional
    public void deleteCommentary(Long commentaryId) {
        if (!commentariesRepository.existsById(commentaryId)) {
            throw new EntityNotFoundException("Commentary with id: " + commentaryId + " not found!");
        }

        commentariesRepository.deleteById(commentaryId);
    }

    private Commentary convertToCommentary(CommentaryRequest commentaryRequest) {
        return modelMapper.map(commentaryRequest, Commentary.class);
    }

    private CommentaryResponse convertToCommentaryResponse(Commentary commentary) {
        return modelMapper.map(commentary, CommentaryResponse.class);
    }
}
