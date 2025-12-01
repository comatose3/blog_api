package com.example.BlogAPI.post;

import com.example.BlogAPI.post.dto.PostRequest;
import com.example.BlogAPI.post.dto.PostResponse;
import com.example.BlogAPI.post.dto.PostUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private final PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping("/")
    public String hello() {
        return "Привет из Docker! Это Java Spring Boot приложение!";
    }

    @GetMapping()
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> allPosts = postsService.getAllPosts();
        return new ResponseEntity<>(allPosts, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        PostResponse post = postsService.getPostByIdWithComments(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new  ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        PostResponse createdPost = postsService.writePost(postRequest);


        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdate postUpdate,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Post ID is invalid", HttpStatus.BAD_REQUEST);
        }

        PostResponse updatedPost = postsService.updatePost(id, postUpdate);

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postsService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
