package net.sparkminds.librarymanagement.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.payload.request.OtpDto;
import net.sparkminds.librarymanagement.payload.request.RegisterDto;
import net.sparkminds.librarymanagement.payload.request.ResendDto;
import net.sparkminds.librarymanagement.payload.response.MessageResponse;
import net.sparkminds.librarymanagement.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static net.sparkminds.librarymanagement.utils.AppConstants.*;

@RestController
@RequestMapping("api/v1/")
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
    public ResponseEntity<MessageResponse> verifyUserByToken(@RequestParam("code") String code) {
        String tokenStatus = registerService.verifyToken(code);
        ResponseEntity<MessageResponse> response;

        if (tokenStatus.equals(TOKEN_INVALID)) {
            response = new ResponseEntity<>(new MessageResponse("Verify email fail, token invalid!"), HttpStatus.UNAUTHORIZED);
        } else if (tokenStatus.equals(TOKEN_EXPIRE)) {
            response = new ResponseEntity<>(new MessageResponse("Verify email fail, token expire!"), HttpStatus.FORBIDDEN);
        } else if (tokenStatus.equals(TOKEN_VALID)) {
            response = new ResponseEntity<>(new MessageResponse("Verify email successfully!"), HttpStatus.OK);
        } else {
            throw new ResourceInvalidException("User already enabled", "");
        }

        return response;
    }

    /**
     * verify email by OTP
     *
     * @param otpDto otpDto
     * @return {@link ResponseEntity}
     */
    @PostMapping("/verify/otp")
    public ResponseEntity<MessageResponse> verifyUserByOtp(@RequestBody @Valid OtpDto otpDto) {
        String otpStatus = registerService.verifyOtp(otpDto);
        ResponseEntity<MessageResponse> response;

        if (otpStatus.equals(OTP_INVALID)) {
            response = new ResponseEntity<>(new MessageResponse("Verify email fail, OTP invalid!"), HttpStatus.UNAUTHORIZED);
        } else if (otpStatus.equals(OTP_EXPIRE)) {
            response = new ResponseEntity<>(new MessageResponse("Verify email fail, OTP expire!"), HttpStatus.FORBIDDEN);
        } else if (otpStatus.equals(OTP_VALID)) {
            response = new ResponseEntity<>(new MessageResponse("Verify email successfully!"), HttpStatus.OK);
        } else {
            throw new ResourceInvalidException("User already enabled", "");
        }

        return response;
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
