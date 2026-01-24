package com.example.BlogAPI.post;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.kafka.events.PostCreatedEvent;
import com.example.BlogAPI.kafka.events.PostUpdatedEvent;
import com.example.BlogAPI.kafka.producer.KafkaProducerService;
import com.example.BlogAPI.post.dto.PostRequest;
import com.example.BlogAPI.post.dto.PostResponse;
import com.example.BlogAPI.post.dto.PostUpdate;
import com.example.BlogAPI.tag.Tag;
import com.example.BlogAPI.user.User;
import com.example.BlogAPI.user.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class PostsService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;
    private final KafkaProducerService kafkaProducer;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public PostsService(PostsRepository postsRepository,
                        UsersRepository usersRepository,
                        ModelMapper modelMapper,
                        KafkaProducerService kafkaProducer,
                        ApplicationEventPublisher eventPublisher) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.kafkaProducer = kafkaProducer;
        this.eventPublisher = eventPublisher;
    }

    public List<PostResponse> getAllPosts() {
        log.info("Fetching all posts");
        return postsRepository.findAll().stream()
                .map(this::convertToPostResponseWithoutComments)
                .collect(Collectors.toList());
    }

    public PostResponse getPostByIdWithComments(Long id) {
        Post post = postsRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post with id " + id + " not found"));

        return convertToPostResponseWithComments(post);
    }

    @Transactional
    public PostResponse writePost(PostRequest postRequest) {
        log.info("Creating new post: {}", postRequest.getName());

        Post post = convertToPost(postRequest);

        if (postRequest.getUser() != null &&
                postRequest.getUser().getUsername() != null) {

            User user = usersRepository.findByUsername(
                            postRequest.getUser().getUsername())
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            post.setUser(user);
        }

        Post savedPost = postsRepository.save(post);

        eventPublisher.publishEvent(new PostCreatedEvent(savedPost.getId()));

        return convertToPostResponseWithoutComments(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdate postUpdate) {
        log.info("Updating post: {}", id);

        Post post = postsRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post not found"));

        post.setName(postUpdate.getName());
        post.setContent(postUpdate.getContent());

        Post updatedPost = postsRepository.save(post);

        sendPostUpdatedEvent(updatedPost);

        return convertToPostResponseWithoutComments(updatedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postsRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found");
        }

        postsRepository.deleteById(id);
        log.info("Post deleted: {}", id);
    }

    private void sendPostCreatedEvent(Post post) {
        try {
            PostCreatedEvent event = new PostCreatedEvent(
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

            kafkaProducer.sendPostCreatedEvent(event);
        } catch (Exception e) {
            log.error("Kafka PostCreatedEvent failed", e);
        }
    }

    private void sendPostUpdatedEvent(Post post) {
        try {
            PostUpdatedEvent event = new PostUpdatedEvent(
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

            kafkaProducer.sendPostUpdatedEvent(event);
        } catch (Exception e) {
            log.error("Kafka PostUpdatedEvent failed", e);
        }
    }

    private Post convertToPost(PostRequest postRequest) {
        return modelMapper.map(postRequest, Post.class);
    }

    private PostResponse convertToPostResponseWithoutComments(Post post) {
        return modelMapper.map(post, PostResponse.class);
    }

    private PostResponse convertToPostResponseWithComments(Post post) {
        PostResponse response = modelMapper.map(post, PostResponse.class);

        List<CommentaryRequest> comments = post.getComments().stream()
                .map(c -> modelMapper.map(c, CommentaryRequest.class))
                .collect(Collectors.toList());

        response.setCommentaryRequests(comments);
        return response;
    }
}
