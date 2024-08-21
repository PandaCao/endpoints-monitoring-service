package com.example.demo.service;

import com.example.demo.domain.dto.UserDTO;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.exception.NoSuchUserException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(UserDTO user) {
        var newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setAccessToken(user.getAccessToken());

        return userRepository.save(newUser);
    }

    public User updateUser(Long id, UserDTO userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User not found with ID: " + id));

        // Updates only the provided fields
        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getAccessToken() != null) {
            user.setAccessToken(userDetails.getAccessToken());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
