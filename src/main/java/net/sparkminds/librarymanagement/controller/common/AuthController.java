package net.sparkminds.librarymanagement.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.EnableMfaDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.payload.response.JwtResponse;
import net.sparkminds.librarymanagement.payload.response.MFAResponse;
import net.sparkminds.librarymanagement.payload.response.TokenRefreshResponse;
import net.sparkminds.librarymanagement.service.AuthService;
import net.sparkminds.librarymanagement.service.MfaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/common/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final MfaService mfaService;

    /**
     * login
     *
     * @param loginDto contains email and password
     * @return {@link ResponseEntity}
     */
    @PostMapping(value = {"/login", "/sign-in"})
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    /**
     * refresh token
     *
     * @param tokenRefreshDto tokenRefreshDto
     * @return {@link ResponseEntity}
     */
    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody @Valid TokenRefreshDto tokenRefreshDto) {
        return new ResponseEntity<>(authService.refreshToken(tokenRefreshDto), HttpStatus.OK);
    }

    /**
     * Generate QR and secret key
     * @return
     */
    @GetMapping("/mfa/generate-qr-and-secret")
    public ResponseEntity<MFAResponse> generate2FACode() {
        return new ResponseEntity<>(mfaService.generateMFACode(), HttpStatus.OK);
    }

    /**
     * Enable google authenticator for account
     *
     * @param enableMfaDto contains secretKey, email and otp
     * @return
     */
    @PostMapping("/mfa/enable-google-auth")
    public ResponseEntity<Void> enable2FACode(@RequestBody EnableMfaDto enableMfaDto) {
        mfaService.enableMFACode(enableMfaDto.getSecretKey(), enableMfaDto.getOtp(), enableMfaDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
