package com.example.BlogAPI.comment;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.comment.dto.CommentaryResponse;
import com.example.BlogAPI.comment.dto.CommentaryUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/commentaries")
public class CommentariesController {
    private final CommentariesService commentariesService;

    @Autowired
    public CommentariesController(CommentariesService commentariesService) {
        this.commentariesService = commentariesService;
    }

    @GetMapping()
    public ResponseEntity<List<CommentaryResponse>> getAllCommentaries(@PathVariable Long postId) {
        List<CommentaryResponse> allCommentaries = commentariesService.getAllCommentariesByPost(postId);
        return new ResponseEntity<>(allCommentaries, HttpStatus.OK);
    }

    @GetMapping("/{commentaryId}")
    public ResponseEntity<CommentaryResponse> getCommentaryById(@PathVariable Long postId, @PathVariable Long commentaryId) {
        CommentaryResponse commentary = commentariesService.getCommentaryById(commentaryId);
        return new ResponseEntity<>(commentary, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createCommentary(@PathVariable Long postId, @Valid @RequestBody CommentaryRequest commentaryRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Cannot create a commentary", HttpStatus.BAD_REQUEST);
        }

        CommentaryResponse createdCommentary = commentariesService.writeCommentary(postId, commentaryRequest);

        return new ResponseEntity<>(createdCommentary, HttpStatus.CREATED);
    }

    @PutMapping("/{commentaryId}")
    public ResponseEntity<?> updateCommentary(@PathVariable Long postId, @PathVariable Long commentaryId, @Valid @RequestBody CommentaryUpdate commentaryUpdate,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Commentary ID is invalid",  HttpStatus.BAD_REQUEST);
        }

        CommentaryResponse updatedCommentary = commentariesService.updateCommentary(commentaryId, commentaryUpdate);

        return ResponseEntity.ok(updatedCommentary);
    }

    @DeleteMapping("/{commentaryId}")
    public ResponseEntity<?> deleteCommentary(@PathVariable Long postId, @PathVariable Long commentaryId) {
        commentariesService.deleteCommentary(commentaryId);
        return ResponseEntity.ok().build();
    }

}
