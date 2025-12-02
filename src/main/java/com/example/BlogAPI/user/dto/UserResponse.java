package com.example.BlogAPI.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String password;
    private List<PostRequest> posts;
    private List<CommentaryRequest> comments;
    private int followingCount;
    private int followersCount;

    private boolean isFollowedByCurrentUser;
    private boolean isFollowingCurrentUser;

    // Конструктор из User
    public UserResponse(User user, User currentUser) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.followingCount = user.getFollowingCount();
        this.followersCount = user.getFollowerCount();
        this.isFollowedByCurrentUser = currentUser != null &&
                currentUser.getFollowing().contains(user);
        this.isFollowingCurrentUser = currentUser != null &&
                user.getFollowing().contains(currentUser);
    }
}
