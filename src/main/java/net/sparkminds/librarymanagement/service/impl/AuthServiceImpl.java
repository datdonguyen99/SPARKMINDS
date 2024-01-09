package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.exception.LibraryManagementException;
import net.sparkminds.librarymanagement.payload.RegisterDTO;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public String register(RegisterDTO registerDTO) {
        // check for email exists in db
        boolean existEmail = userRepository.existsByEmail(registerDTO.getEmail());
        if (existEmail) {
            throw new LibraryManagementException(HttpStatus.BAD_REQUEST, "Email is already exist!");
        }

        // create user entity using Builder pattern
        User user = User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();

        // save user entity to db
        userRepository.save(user);

        return "User registered successfully!";
    }
}
