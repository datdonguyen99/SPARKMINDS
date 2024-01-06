package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.RegisterDTO;

public interface AuthService {
    String register(RegisterDTO registerDTO);
}
