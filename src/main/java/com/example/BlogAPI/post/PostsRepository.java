package com.example.BlogAPI.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Long> {
    Post getPostById(Long id);
    List<Post> findByUserId(Long userId);
}
