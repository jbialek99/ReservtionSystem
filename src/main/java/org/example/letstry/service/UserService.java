package org.example.letstry.service;

import org.example.letstry.model.User;
import org.example.letstry.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public void addUserAttributesToModel(OAuth2User principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getAttribute("name"));
            model.addAttribute("email", principal.getAttribute("email"));
        }
    }
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}