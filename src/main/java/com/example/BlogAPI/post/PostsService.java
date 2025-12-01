package com.example.BlogAPI.post;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.post.dto.PostRequest;
import com.example.BlogAPI.post.dto.PostResponse;
import com.example.BlogAPI.post.dto.PostUpdate;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostsService {
    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PostsService(PostsRepository postsRepository, ModelMapper modelMapper) {
        this.postsRepository = postsRepository;
        this.modelMapper = modelMapper;
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
        return convertToPostResponseWithoutComments(postsRepository.save(convertToPost(postRequest)));
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdate postUpdate) {
        Post postToUpdate = postsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));

        postToUpdate.setName(postUpdate.getName());
        postToUpdate.setContent(postUpdate.getContent());

        return convertToPostResponseWithoutComments(postsRepository.save(postToUpdate));
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

        postResponse.setCommentaryResponses(commentaryRequests);
        return postResponse;
    }
}
