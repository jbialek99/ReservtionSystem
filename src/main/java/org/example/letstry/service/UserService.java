package org.example.letstry.service;

import org.example.letstry.model.User;
import org.example.letstry.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserById(Long userId){
        return userRepository.findById(userId);
    }
}
