package com.example.BlogAPI.controllers;

import com.example.BlogAPI.dto.CommentaryRequest;
import com.example.BlogAPI.dto.CommentaryUpdate;
import com.example.BlogAPI.entities.Commentary;
import com.example.BlogAPI.services.CommentariesService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/commentaries")
public class CommentariesController {
    private final CommentariesService commentariesService;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentariesController(CommentariesService commentariesService, ModelMapper modelMapper) {
        this.commentariesService = commentariesService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<List<Commentary>> getAllCommentaries(@PathVariable Integer postId) {
        List<Commentary> allCommentaries = commentariesService.getAllCommentariesByPost(postId);
        return new ResponseEntity<>(allCommentaries, HttpStatus.OK);
    }

    @GetMapping("/{commentaryId}")
    public ResponseEntity<Commentary> getCommentaryById(@PathVariable Integer postId, @PathVariable Integer commentaryId) {
        Commentary commentary = commentariesService.getCommentaryById(commentaryId);
        return new ResponseEntity<>(commentary, HttpStatus.OK);
    }

    @PostMapping("/add_commentary")
    public ResponseEntity<?> createCommentary(@PathVariable Integer postId, @Valid @RequestBody CommentaryRequest commentaryRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Cannot create a commentary", HttpStatus.BAD_REQUEST);
        }

        Commentary createdCommentary = commentariesService.writeCommentary(postId, commentaryRequest);

        return new ResponseEntity<>(createdCommentary, HttpStatus.CREATED);
    }

    @PutMapping("/{commentaryId}")
    public ResponseEntity<?> updateCommentary(@PathVariable Integer postId, @PathVariable Integer commentaryId, @Valid @RequestBody CommentaryUpdate commentaryUpdate,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Commentary ID is invalid",  HttpStatus.BAD_REQUEST);
        }

        Commentary updatedCommentary = commentariesService.updateCommentary(commentaryId, commentaryUpdate);

        return ResponseEntity.ok(updatedCommentary);
    }

    @DeleteMapping("/{commentaryId}")
    public ResponseEntity<?> deleteCommentary(@PathVariable Integer postId, @PathVariable Integer commentaryId) {
        commentariesService.deleteCommentary(commentaryId);
        return ResponseEntity.ok().build();
    }

}
