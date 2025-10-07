package com.example.BlogAPI.controllers;

import com.example.BlogAPI.dto.PostRequest;
import com.example.BlogAPI.dto.PostUpdate;
import com.example.BlogAPI.entities.Post;
import com.example.BlogAPI.services.PostsService;
import com.example.BlogAPI.util.PostValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostsController {

    private final PostsService postsService;
    private final ModelMapper modelMapper;
    private final PostValidator postValidator;

    @Autowired
    public PostsController(PostsService postsService, PostValidator postValidator, ModelMapper modelMapper) {
        this.postsService = postsService;
        this.postValidator = postValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/")
    public String hello() {
        return "Привет из Docker! Это Java Spring Boot приложение!";
    }

    @GetMapping()
    public List<Post> findAll() {
        List<Post> posts = postsService.getAllPosts();
        return posts;
    }

    @PostMapping("/write")
    public String createPost(@Valid @RequestBody PostRequest postRequest, BindingResult bindingResult) {
        Post postToWrite = convertToPost(postRequest);

        if (bindingResult.hasErrors()) {
            return "/posts/write";
        }

        postsService.writePost(postToWrite);
        return "redirect:/posts";
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Integer id, @Valid @RequestBody PostUpdate postUpdate,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Post ID is invalid", HttpStatus.BAD_REQUEST);
        }

        Post updatedPost = postsService.updatePost(id, postUpdate);

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        postsService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    private Post convertToPost(PostRequest postRequest) {
        return modelMapper.map(postRequest, Post.class);
    }

}
