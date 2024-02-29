package net.sparkminds.librarymanagement.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.EnableMfaDto;
import net.sparkminds.librarymanagement.payload.response.MFAResponse;
import net.sparkminds.librarymanagement.service.MfaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class MfaController {
    private final MfaService mfaService;

    /**
     * Generate QR and secret key
     *
     * @return {@link ResponseEntity}
     */
    @Operation(summary = "Generate 2FA", description = "User wants to enable 2-step authentication when logging in the next time", tags = {"MFA functions"})
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
    @Operation(summary = "Enable 2FA", description = "User wants to enable 2-step authentication when logging in the next time", tags = {"MFA functions"})
    @PostMapping("/mfa/enable-google-auth")
    public ResponseEntity<Void> enable2FACode(@RequestBody @Valid EnableMfaDto enableMfaDto) {
        mfaService.enableMFACode(enableMfaDto.getSecretKey(), enableMfaDto.getOtp());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
