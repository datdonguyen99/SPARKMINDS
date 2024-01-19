package net.sparkminds.librarymanagement.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.OtpDto;
import net.sparkminds.librarymanagement.payload.request.RegisterDto;
import net.sparkminds.librarymanagement.payload.request.ResendDto;
import net.sparkminds.librarymanagement.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/common/")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;

    /**
     * register user
     *
     * @param registerDTO registerDTO
     * @return {@link ResponseEntity}
     */
    @PostMapping(value = {"/register", "/sign-up"})
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDto registerDTO) {
        registerService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * verify email by link token
     *
     * @param code code
     * @return {@link ResponseEntity}
     */
    @GetMapping("/verify/token")
    public ResponseEntity<Void> verifyUserByToken(@RequestParam("code") String code) {
        registerService.verifyToken(code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * verify email by OTP
     *
     * @param otpDto otpDto
     * @return {@link ResponseEntity}
     */
    @PostMapping("/verify/otp")
    public ResponseEntity<Void> verifyUserByOtp(@RequestBody @Valid OtpDto otpDto) {
        registerService.verifyOtp(otpDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * resend token
     *
     * @param resendDto resendDto
     * @return {@link ResponseEntity}
     */
    @PostMapping("/resend/token")
    public ResponseEntity<Void> resendToken(@RequestBody @Valid ResendDto resendDto) {
        registerService.resendToken(resendDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * resend otp
     *
     * @param resendDto resendDto
     * @return {@link ResponseEntity}
     */
    @PostMapping("/resend/otp")
    public ResponseEntity<Void> resendOtp(@RequestBody @Valid ResendDto resendDto) {
        registerService.resendOTP(resendDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
