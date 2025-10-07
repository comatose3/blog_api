package com.example.BlogAPI.repositories;

import com.example.BlogAPI.entities.Commentary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentariesRepository extends JpaRepository<Commentary, Integer> {

}
