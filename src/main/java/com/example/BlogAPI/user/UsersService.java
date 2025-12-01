package com.example.BlogAPI.user;

import com.example.BlogAPI.user.dto.UserRequest;
import com.example.BlogAPI.user.dto.UserResponse;
import com.example.BlogAPI.user.dto.UserUpdate;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UsersService {
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UsersRepository usersRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return usersRepository.findAll().stream()
                .map(user -> convertToUserResponse(user))
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = usersRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found!"));
        return convertToUserResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        return convertToUserResponse(usersRepository.save(convertToUser(userRequest)));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdate userUpdate) {
        User userToUpdate = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found!"));

        userToUpdate.setUsername(userUpdate.getUsername());

        return convertToUserResponse(usersRepository.save(userToUpdate));
    }

    @Transactional
    public void deleteUserById(Long id) {
        if(!usersRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found!");
        }
        usersRepository.deleteById(id);
    }

    private User convertToUser(UserRequest userRequest) {
        return modelMapper.map(userRequest, User.class);
    }

    private UserResponse convertToUserResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }
}
