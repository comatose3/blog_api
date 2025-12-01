package com.example.BlogAPI.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentariesRepository extends JpaRepository<Commentary, Long> {
    List<Commentary> findByPostId(Long postId);
}
