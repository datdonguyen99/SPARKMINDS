package net.sparkminds.librarymanagement.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.payload.OtpDto;
import net.sparkminds.librarymanagement.payload.RegisterDto;
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
@RequestMapping("api/v1/auth")
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
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterDto registerDTO) {
        registerService.register(registerDTO);
        return new ResponseEntity<>("Register successfully!", HttpStatus.CREATED);
    }

    /**
     * verify email by link token
     *
     * @param code code
     * @return {@link ResponseEntity}
     */
    @GetMapping("/verify/token")
    public ResponseEntity<Object> verifyUserByToken(@RequestParam("code") String code) {
        String tokenStatus = registerService.verifyToken(code);
        ResponseEntity<Object> response;

        switch (tokenStatus) {
            case "":
                throw new ResourceInvalidException("User already enabled", "");

            case TOKEN_INVALID:
                response = new ResponseEntity<>("Verify email fail, token invalid!", HttpStatus.UNAUTHORIZED);
                break;

            case TOKEN_EXPIRE:
                response = new ResponseEntity<>("Verify email fail, token expire!", HttpStatus.FORBIDDEN);
                break;

            default:
                response = new ResponseEntity<>("Verify email successfully!", HttpStatus.OK);
                break;
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
    public ResponseEntity<Object> verifyUserByOtp(@RequestBody @Valid OtpDto otpDto) {
        String otpStatus = registerService.verifyOtp(otpDto);
        ResponseEntity<Object> response;

        if (otpStatus.equals(OTP_INVALID)) {
            response = new ResponseEntity<>("Verify email fail, OTP invalid!", HttpStatus.UNAUTHORIZED);
        } else if (otpStatus.equals(OTP_EXPIRE)) {
            response = new ResponseEntity<>("Verify email fail, OTP expire!", HttpStatus.FORBIDDEN);
        } else if (otpStatus.equals(OTP_VALID)) {
            response = new ResponseEntity<>("Verify email successfully!", HttpStatus.OK);
        } else {
            throw new ResourceInvalidException("User already enabled", "");
        }

        return response;
    }
}
