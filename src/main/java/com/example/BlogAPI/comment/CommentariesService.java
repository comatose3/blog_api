package com.example.BlogAPI.comment;

import com.example.BlogAPI.comment.dto.CommentaryRequest;
import com.example.BlogAPI.comment.dto.CommentaryResponse;
import com.example.BlogAPI.comment.dto.CommentaryUpdate;
import com.example.BlogAPI.post.PostsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        return convertToCommentaryResponse(commentariesRepository.save(convertToCommentary(commentaryRequest)));
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
