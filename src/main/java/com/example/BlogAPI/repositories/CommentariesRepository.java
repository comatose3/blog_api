package com.example.BlogAPI.repositories;

import com.example.BlogAPI.entities.Commentary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentariesRepository extends JpaRepository<Commentary, Integer> {
    List<Commentary> findByPostId(Integer postId);
}
