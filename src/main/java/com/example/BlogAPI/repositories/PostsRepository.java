package com.example.BlogAPI.repositories;

import com.example.BlogAPI.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {
    Post getPostById(Integer id);
}
