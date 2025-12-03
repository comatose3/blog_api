package com.example.BlogAPI.sub;

import com.example.BlogAPI.user.User;
import com.example.BlogAPI.user.UsersRepository;
import com.example.BlogAPI.user.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SubscriptionsService {

    private final UsersRepository usersRepository;

    @Autowired
    public SubscriptionsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<UserResponse> getFollowing(Long userId, User currentUser) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowing().stream()
                .map(following -> new UserResponse(following, currentUser))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getFollowers(Long userId, User currentUser) {
        User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFollowers().stream()
                .map(follower -> new UserResponse(follower, currentUser))
                .collect(Collectors.toList());
    }

    public List<UserResponse> searchUsers(String query, User currentUser) {
        List<User> users = usersRepository.findByUsernameContainingIgnoreCase(query);
        return users.stream()
                .map(user -> new UserResponse(user, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void followUser(Long followerId, Long followingId) {
        User follower = usersRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = usersRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        if (followerId.equals(followingId)) {
            throw new RuntimeException("Cannot follow yourself!");
        }

        if (follower.isFollowing(following)) {
            throw new RuntimeException("You already following this user");
        }

        follower.follow(following);
        usersRepository.save(follower);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        User follower = usersRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User following = usersRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        if (!follower.isFollowing(following)) {
            throw new RuntimeException("Not following this user");
        }

        follower.unfollow(following);
        usersRepository.save(follower);
    }

}
