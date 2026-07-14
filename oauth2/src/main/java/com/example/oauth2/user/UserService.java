package com.example.oauth2.user;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> register(String email, String name, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            return Optional.empty();
        }
        User user = User.builder()
                .email(email)
                .name(name)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        return Optional.of(userRepository.save(user));
    }

    public boolean checkPassword(User user, String rawPassword) {
        return user.getPasswordHash() != null && passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    public User findOrCreateOAuthUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder().email(email).name(name).build()));
    }

}
