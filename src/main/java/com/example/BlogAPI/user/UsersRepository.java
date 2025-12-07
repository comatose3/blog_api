package com.example.BlogAPI.user;

import com.example.BlogAPI.user.dto.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    User getUserById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<UserResponse> findByUsernameContainingIgnoreCase(String username);
    List<UserResponse> findByRolesContaining(UserRole role);
}
