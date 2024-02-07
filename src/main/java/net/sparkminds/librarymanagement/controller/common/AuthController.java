package net.sparkminds.librarymanagement.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.payload.response.JwtResponse;
import net.sparkminds.librarymanagement.payload.response.TokenRefreshResponse;
import net.sparkminds.librarymanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/common")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * login
     *
     * @param loginDto contains email and password
     * @return {@link ResponseEntity}
     */
    @Operation(summary = "Login", description = "Login", tags = {"Authentication functions"})
    @PostMapping(value = {"/auth/login", "/auth/sign-in"})
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    /**
     * refresh token
     *
     * @param tokenRefreshDto tokenRefreshDto
     * @return {@link ResponseEntity}
     */
    @Operation(summary = "Refresh token", description = "Use refresh token to generate new access token", tags = {"Authentication functions"})
    @PostMapping("/auth/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody @Valid TokenRefreshDto tokenRefreshDto) {
        return new ResponseEntity<>(authService.refreshToken(tokenRefreshDto), HttpStatus.OK);
    }
}
