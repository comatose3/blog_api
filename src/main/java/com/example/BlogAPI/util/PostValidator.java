package com.example.BlogAPI.util;

import com.example.BlogAPI.entities.Post;
import com.example.BlogAPI.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PostValidator implements Validator {
    private final PostsService postsService;

    @Autowired
    public PostValidator(PostsService postsService) {
        this.postsService = postsService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(Post.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Post post = (Post) o;

    }
}
