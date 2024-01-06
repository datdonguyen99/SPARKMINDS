package net.sparkminds.librarymanagement.service.impl;

import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.payload.RegisterDTO;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String register(RegisterDTO registerDTO) {
        // check for email exists in db
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email is already exist!");
        }

        // create user entity using Builder pattern
        User user = User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(registerDTO.getPassword())
                .build();

        // save user entity to db
        userRepository.save(user);
        return "User registered successfully!";
    }
}
