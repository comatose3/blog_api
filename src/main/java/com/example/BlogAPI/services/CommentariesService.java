package com.example.BlogAPI.services;

import com.example.BlogAPI.dto.CommentaryRequest;
import com.example.BlogAPI.dto.CommentaryUpdate;
import com.example.BlogAPI.entities.Commentary;
import com.example.BlogAPI.entities.Post;
import com.example.BlogAPI.repositories.CommentariesRepository;
import com.example.BlogAPI.repositories.PostsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentariesService {
    private final CommentariesRepository commentariesRepository;
    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentariesService(CommentariesRepository commentariesRepository, PostsRepository postsRepository, ModelMapper modelMapper) {
        this.commentariesRepository = commentariesRepository;
        this.postsRepository = postsRepository;
        this.modelMapper = modelMapper;
    }

    public List<Commentary> getAllCommentariesByPost(Integer postId) {
        return commentariesRepository.findByPostId(postId);
    }

    public Commentary getCommentaryById(Integer commentaryId) {
        return  commentariesRepository.findById(commentaryId).orElseThrow(() -> new EntityNotFoundException("Commentary with id: " + commentaryId + " not found!"));
    }

    @Transactional
    public Commentary writeCommentary(Integer postId, CommentaryRequest commentaryRequest) {
        Post post = postsRepository.getPostById(postId);

        Commentary commentary = convertToCommentary(commentaryRequest);
        commentary.setPost(post);
        return commentariesRepository.save(commentary);
    }

    @Transactional
    public Commentary updateCommentary(Integer commentaryId, CommentaryUpdate commentaryUpdate) {
        Commentary commentaryToUpdate = getCommentaryById(commentaryId);

        commentaryToUpdate.setText(commentaryUpdate.getText());

        return commentariesRepository.save(commentaryToUpdate);
    }

    @Transactional
    public void deleteCommentary(Integer commentaryId) {
        if (!commentariesRepository.existsById(commentaryId)) {
            throw new EntityNotFoundException("Commentary with id: " + commentaryId + " not found!");
        }

        commentariesRepository.deleteById(commentaryId);
    }

    private Commentary convertToCommentary(CommentaryRequest commentaryRequest) {
        return modelMapper.map(commentaryRequest, Commentary.class);
    }

    private CommentaryUpdate convertToCommentaryUpdate(Commentary commentary) {
        return modelMapper.map(commentary, CommentaryUpdate.class);
    }
}
