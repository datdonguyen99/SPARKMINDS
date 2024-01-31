package net.sparkminds.librarymanagement.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.EnableMfaDto;
import net.sparkminds.librarymanagement.payload.request.ResetPassDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.payload.response.JwtResponse;
import net.sparkminds.librarymanagement.payload.response.MFAResponse;
import net.sparkminds.librarymanagement.payload.response.TokenRefreshResponse;
import net.sparkminds.librarymanagement.service.AuthService;
import net.sparkminds.librarymanagement.service.ChangeUserInfo;
import net.sparkminds.librarymanagement.service.MfaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final MfaService mfaService;

    private final ChangeUserInfo changeUserInfo;

    /**
     * login
     *
     * @param loginDto contains email and password
     * @return {@link ResponseEntity}
     */
    @PostMapping(value = {"/common/auth/login", "/common/auth/sign-in"})
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    /**
     * refresh token
     *
     * @param tokenRefreshDto tokenRefreshDto
     * @return {@link ResponseEntity}
     */
    @PostMapping("/common/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody @Valid TokenRefreshDto tokenRefreshDto) {
        return new ResponseEntity<>(authService.refreshToken(tokenRefreshDto), HttpStatus.OK);
    }

    /**
     * Generate QR and secret key
     *
     * @return {@link ResponseEntity}
     */
    @GetMapping("/mfa/generate-qr-and-secret")
    public ResponseEntity<MFAResponse> generate2FACode() {
        return new ResponseEntity<>(mfaService.generateMFACode(), HttpStatus.OK);
    }

    /**
     * Enable google authenticator for account
     *
     * @param enableMfaDto contains secretKey and otp
     * @return
     */
    @PostMapping("/mfa/enable-google-auth")
    public ResponseEntity<Void> enable2FACode(@RequestBody @Valid EnableMfaDto enableMfaDto) {
        mfaService.enableMFACode(enableMfaDto.getSecretKey(), enableMfaDto.getOtp());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * logout
     *
     * @param authorizationHeader
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(@RequestHeader(value = "Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     *
     * @param resetPassDto contain email and new password
     * @return
     */

    @PutMapping("/common/reset-password")
    public ResponseEntity<Void> resetPass(@RequestBody @Valid ResetPassDto resetPassDto) {
        changeUserInfo.resetPassword(resetPassDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
