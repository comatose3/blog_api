package com.example.BlogAPI.user;

import com.example.BlogAPI.sub.SubscriptionsService;
import com.example.BlogAPI.user.dto.UserRequest;
import com.example.BlogAPI.user.dto.UserResponse;
import com.example.BlogAPI.user.dto.UserUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private SubscriptionsService subscriptionsService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = usersService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = usersService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest userRequest,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Cannot create a user", HttpStatus.BAD_REQUEST);
        }

        UserResponse createdUser = usersService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdate userUpdate, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>("Cannot update a user", HttpStatus.BAD_REQUEST);
        }

        UserResponse updatedUser = usersService.updateUser(id, userUpdate);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        usersService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> followUser(@PathVariable Long userId, Authentication authentication) {
        UserResponse currentUser = getCurrentUser(authentication);
        UserResponse userToFollow = usersService.getUserById(userId);

        String username = userToFollow.getUsername();

        subscriptionsService.followUser(currentUser.getId(), userToFollow.getId());

        return ResponseEntity.ok(Map.of("message", "Successfully followed " + username));
    }

    @PostMapping("/{userId}/unfollow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userId, Authentication authentication) {
        UserResponse currentUser = getCurrentUser(authentication);
        UserResponse userToUnfollow = usersService.getUserById(userId);

        String username = userToUnfollow.getUsername();

        subscriptionsService.unfollowUser(currentUser.getId(), userToUnfollow.getId());

        return ResponseEntity.ok(Map.of("message", "Successfully unfollowed " + username));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserResponse>> getFollowingUsers(@PathVariable Long userId, Authentication authentication) {
        UserResponse currentUser = getCurrentUser(authentication);
        UserResponse user = usersService.getUserById(userId);

        List<UserResponse> following = subscriptionsService.getFollowing(user.getId(), currentUser);

        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/follower")
    public ResponseEntity<List<UserResponse>> getFollowers(@PathVariable Long userId, Authentication authentication) {
        UserResponse currentUser = getCurrentUser(authentication);
        UserResponse user = usersService.getUserById(userId);

        List<UserResponse> followers = subscriptionsService.getFollowers(user.getId(), currentUser);

        return ResponseEntity.ok(followers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String q, Authentication authentication) {
        UserResponse currentUser = getCurrentUser(authentication);
        List<UserResponse> users = usersService.searchUsers(q);

        return ResponseEntity.ok(users);
    }

    private UserResponse getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return usersService.getUserByUsername(username);
    }
}
