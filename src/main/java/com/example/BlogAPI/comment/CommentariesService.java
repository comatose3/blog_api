package com.example.BlogAPI.comment;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.comment.dto.CommentaryResponse;
import com.example.BlogAPI.comment.dto.CommentaryUpdate;
import com.example.BlogAPI.kafka.events.CommentCreatedEvent;
import com.example.BlogAPI.kafka.events.CommentUpdatedEvent;
import com.example.BlogAPI.kafka.producer.KafkaProducerService;
import com.example.BlogAPI.post.Post;
import com.example.BlogAPI.post.PostsRepository;
import com.example.BlogAPI.user.User;
import com.example.BlogAPI.user.UsersRepository;
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
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public CommentariesService(CommentariesRepository commentariesRepository, PostsRepository postsRepository, UsersRepository usersRepository, ModelMapper modelMapper, KafkaProducerService kafkaProducerService) {
        this.commentariesRepository = commentariesRepository;
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
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


        User user = usersRepository.findByUsername(commentaryRequest.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("User with name " + commentaryRequest.getUser().getUsername() + " not found"));

        Commentary commentary = convertToCommentary(commentaryRequest);
        commentary.setPost(post);
        commentary.setUser(user);

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
        log.info("Updating comment: {}",  commentaryId);
        Commentary commentaryToUpdate = commentariesRepository.findById(commentaryId)
                .orElseThrow(() -> new EntityNotFoundException("Commentary with id: " + commentaryId + " not found"));

        commentaryToUpdate.setText(commentaryUpdate.getText());

        Commentary updatedCommentary = commentariesRepository.save(commentaryToUpdate);

        try {
            CommentUpdatedEvent event = new CommentUpdatedEvent(
                    updatedCommentary.getId(),
                    updatedCommentary.getText(),
                    updatedCommentary.getPost().getId(),
                    updatedCommentary.getPost().getName(),
                    updatedCommentary.getUser().getId(),
                    updatedCommentary.getUser().getUsername()
            );

            kafkaProducerService.sendCommentUpdatedEvent(event);
            log.info("Comment updated and event sent to Kafka: commentId={}", updatedCommentary.getId());
        } catch (Exception e) {
            log.error("Failed to send CommentUpdatedEvent to Kafka: {}", e.getMessage());
        }

        return convertToCommentaryResponse(updatedCommentary);
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
