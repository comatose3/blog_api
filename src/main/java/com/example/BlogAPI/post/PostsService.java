package com.example.BlogAPI.post;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.kafka.events.PostCreatedEvent;
import com.example.BlogAPI.kafka.events.PostUpdatedEvent;
import com.example.BlogAPI.kafka.producer.KafkaProducerService;
import com.example.BlogAPI.post.dto.PostRequest;
import com.example.BlogAPI.post.dto.PostResponse;
import com.example.BlogAPI.post.dto.PostUpdate;
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
public class PostsService {
    private final PostsRepository postsRepository;
    private final UsersRepository  usersRepository;
    private final ModelMapper modelMapper;
    private final KafkaProducerService kafkaProducer;

    @Autowired
    public PostsService(PostsRepository postsRepository, UsersRepository  usersRepository, ModelMapper modelMapper, KafkaProducerService kafkaProducer) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PostResponse> getAllPosts() {
        return postsRepository.findAll().stream()
                .map(post -> convertToPostResponseWithoutComments(post))
                .collect(Collectors.toList());
    }

    public PostResponse getPostByIdWithComments(Long id) {
        Post post = postsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));

        return convertToPostResponseWithComments(post);
    }

    @Transactional
    public PostResponse writePost(PostRequest postRequest) {
        log.info("Creating new post: {}", postRequest.getName());

        Post post = convertToPost(postRequest);

        if (postRequest.getUser() != null && postRequest.getUser().getUsername() != null) {
            User user = usersRepository.findByUsername(postRequest.getUser().getUsername())
                    .orElseThrow(() -> new RuntimeException("User with name " + postRequest.getUser().getUsername() + " not found"));
            post.setUser(user);
        }

        Post savedPost = postsRepository.save(post);

        try {
            PostCreatedEvent event = new PostCreatedEvent(
                    savedPost.getId(),
                    savedPost.getUser().getId(),
                    savedPost.getUser().getUsername(),
                    savedPost.getName(),
                    savedPost.getContent(),
                    savedPost.getTags() != null ?
                            savedPost.getTags().stream()
                                    .map(tag -> tag.getName())
                                    .collect(Collectors.toList()) :
                            List.of(),
                    savedPost.getCreatedAt()
            );

            kafkaProducer.sendPostCreatedEvent(event);
            log.info("Post created and event sent to Kafka: postId={}", savedPost.getId());
        } catch (Exception e) {
            log.error("Failed to send PostCreatedEvent to Kafka: {}", e.getMessage());
        }

        return convertToPostResponseWithoutComments(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdate postUpdate) {
        log.info("Updating post: {}", id);
        Post postToUpdate = postsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));

        postToUpdate.setName(postUpdate.getName());
        postToUpdate.setContent(postUpdate.getContent());

        Post updatedPost = postsRepository.save(postToUpdate);

        try {
            PostUpdatedEvent event = new PostUpdatedEvent(
                    updatedPost.getId(),
                    updatedPost.getName(),
                    updatedPost.getContent(),
                    updatedPost.getTags() != null ?
                            updatedPost.getTags().stream()
                                    .map(tag -> tag.getName())
                                    .collect(Collectors.toList()) :
                            List.of(),
                    updatedPost.getUser().getId(),
                    updatedPost.getUpdatedAt()
            );

            kafkaProducer.sendPostUpdatedEvent(event);
            log.info("Post updated and event sent to Kafka: postId={}", updatedPost.getId());
        } catch (Exception e) {
            log.error("Failed to send PostUpdatedEvent to Kafka: {}", e.getMessage());
        }

        return convertToPostResponseWithoutComments(updatedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postsRepository.existsById(id)) {
            throw new EntityNotFoundException("Post not found with id: " + id);
        }

        postsRepository.deleteById(id);
    }

    private Post convertToPost(PostRequest postRequest) {
        return modelMapper.map(postRequest, Post.class);
    }

    private PostResponse convertToPostResponseWithoutComments(Post post) {
        return modelMapper.map(post, PostResponse.class);
    }

    private PostResponse convertToPostResponseWithComments(Post post) {
        PostResponse postResponse = modelMapper.map(post, PostResponse.class);

        List<CommentaryRequest> commentaryRequests = post.getComments().stream()
                .map(comment -> modelMapper.map(comment, CommentaryRequest.class))
                .collect(Collectors.toList());

        postResponse.setCommentaryRequests(commentaryRequests);
        return postResponse;
    }
}
