package com.example.BlogAPI;

import com.example.BlogAPI.post.PostsService;
import com.example.BlogAPI.post.dto.PostRequest;
import com.example.BlogAPI.post.dto.PostResponse;
import com.example.BlogAPI.user.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

//@SpringBootTest
//public class PostsServiceIntegrationTest {
//    private final PostsService postsService;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    public PostsServiceIntegrationTest(PostsService postsService, RedisTemplate<String, Object> redisTemplate) {
//        this.postsService = postsService;
//        this.redisTemplate = redisTemplate;
//    }
//
//    @BeforeEach
//    void setUp() {
//        redisTemplate.getConnectionFactory().getConnection().flushAll();
//    }
//
//    @Test
//    void testViewCountIncrement() {
//        Long postId = 1L;
//        String visitorId = "test-visitor-123";
//
//        Long initialViews = postsService.getViewCount(postId);
//
//        postsService.getPostWithAnalytics(postId, visitorId);
//
//        Long newViews = postsService.getViewCount(postId);
//        assertEquals(initialViews + 1, newViews);
//    }
//
//    @Test
//    void testUniqueViewsTracking() {
//        Long postId = 1L;
//        String visitor1 = "visitor-1";
//        String visitor2 = "visitor-2";
//
//        postsService.trackUniqueView(postId, visitor1);
//        postsService.trackUniqueView(postId, visitor1);
//
//        postsService.trackUniqueView(postId, visitor2);
//
//        Long uniqueViews = postsService.getUniqueViewCount(postId);
//        assertEquals(2L, uniqueViews);
//    }
//
//    @Test
//    void testRateLimit() {
//        String userId = "test-user-123";
//
//        int remaining = postsService.getRemainingPostsLimit(userId, 10, 3600);
//        assertEquals(10, remaining);
//        UserRequest userRequest = new UserRequest();
//
//        userRequest.setUsername("Mason");
//        userRequest.setEmail("mason@gmail.com");
//        userRequest.setPassword("password");
//        for (int i = 0; i < 5; i++) {
//            PostRequest request = new PostRequest();
//
//            request.setUser(userRequest);
//            request.setName("Test Post" + i);
//            request.setContent("Content");
//
//            postsService.writePost(request, userId);
//        }
//
//        remaining = postsService.getRemainingPostsLimit(userId, 10, 3600);
//        assertEquals(5, remaining);
//    }
//
//    @Test
//    void testTrendingPosts() {
//        Long postId1 = 1L;
//        Long postId2 = 2L;
//
//        for (int i = 0; i < 100; i++) {
//            postsService.incrementViewCount(postId1);
//        }
//
//        for (int i = 0; i < 50; i++) {
//            postsService.incrementViewCount(postId2);
//        }
//
//        postsService.updateTrendingScore(postId1);
//        postsService.updateTrendingScore(postId2);
//
//        List<PostResponse> trending =  postsService.getTrendingPosts(10);
//
//        assertFalse(trending.isEmpty());
//        assertEquals(postId1, trending.get(0).getId());
//    }
//}
