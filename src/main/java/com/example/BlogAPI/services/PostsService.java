package com.example.BlogAPI.services;

import com.example.BlogAPI.dto.PostUpdate;
import com.example.BlogAPI.entities.Post;
import com.example.BlogAPI.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostsService {
    private final PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public List<Post> getAllPosts() {
        return postsRepository.findAll();
    }

    public Post getPostById(Integer id) {
        return postsRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Transactional
    public void writePost(Post post) {
        postsRepository.save(post);
    }

    @Transactional
    public Post updatePost(Integer id, PostUpdate postUpdate) {
        Post post = getPostById(id);
        post.setName(postUpdate.getName());
        post.setContent(postUpdate.getContent());

        return post;
    }

    @Transactional
    public void deletePost(Integer id) {
        if (!postsRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }

        postsRepository.deleteById(id);
    }
}
