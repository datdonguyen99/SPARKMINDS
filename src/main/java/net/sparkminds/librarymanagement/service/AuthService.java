package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.payload.response.JwtResponse;
import net.sparkminds.librarymanagement.payload.response.TokenRefreshResponse;

public interface AuthService {
    JwtResponse login(LoginDto loginDto);

    TokenRefreshResponse refreshToken(TokenRefreshDto tokenRefreshDto);

    void logout(String authorizationHeader);
}