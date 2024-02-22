package net.sparkminds.librarymanagement.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.ChangeEmailDto;
import net.sparkminds.librarymanagement.payload.request.ChangePassDto;
import net.sparkminds.librarymanagement.payload.request.ChangePhoneNumberDto;
import net.sparkminds.librarymanagement.payload.request.VerifyChangePhoneNumberDto;
import net.sparkminds.librarymanagement.service.ChangeUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class ChangeInfoController {
    private final ChangeUserInfo changeUserInfo;

    @Operation(summary = "Change password", description = "Change password", tags = {"Change User Info functions"})
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePassDto changePassDto) {
        changeUserInfo.changePassword(changePassDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Change email", description = "Change email", tags = {"Change User Info functions"})
    @PutMapping("/change-email")
    public ResponseEntity<Void> changeEmail(@RequestBody @Valid ChangeEmailDto changeEmailDto) {
        changeUserInfo.changeEmail(changeEmailDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Change phone number", description = "Change phone number", tags = {"Change User Info functions"})
    @PutMapping("/change-phone-number")
    public ResponseEntity<Void> sendOTPToPhone(@RequestBody @Valid ChangePhoneNumberDto phoneNumberDto) {
        changeUserInfo.changePhoneNumber(phoneNumberDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Verify change phone", description = "Use OTP to verify when user change phone number", tags = {"Change User Info functions"})
    @PostMapping("/verify/change-phone-number")
    public ResponseEntity<Void> verifyChangePhoneNumber(@RequestBody @Valid VerifyChangePhoneNumberDto changePhoneNumberDto) {
        changeUserInfo.verifyChangePhoneNumber(changePhoneNumberDto.getPhoneNumber(), changePhoneNumberDto.getOtp());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
