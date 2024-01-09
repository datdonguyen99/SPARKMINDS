package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Role;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.exception.LibraryManagementException;
import net.sparkminds.librarymanagement.payload.RegisterDTO;
import net.sparkminds.librarymanagement.repository.RoleRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.service.AuthService;
import net.sparkminds.librarymanagement.utils.RoleName;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;


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

        // set role is USER by default
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new LibraryManagementException(HttpStatus.BAD_REQUEST, "Role not found"));
        user.setRoles(Collections.singleton(role));

        // save user entity to db
        userRepository.save(user);

        return "User registered successfully!";
    }
}
