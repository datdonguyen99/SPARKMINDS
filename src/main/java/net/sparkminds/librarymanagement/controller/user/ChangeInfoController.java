package net.sparkminds.librarymanagement.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.ChangeEmailDto;
import net.sparkminds.librarymanagement.payload.request.ChangePassDto;
import net.sparkminds.librarymanagement.payload.request.ChangePhoneNumberDto;
import net.sparkminds.librarymanagement.service.ChangeUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ChangeInfoController {
    private final ChangeUserInfo changeUserInfo;

    @PutMapping("/user/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePassDto changePassDto) {
        changeUserInfo.changePassword(changePassDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/user/change-email")
    public ResponseEntity<Void> changeEmail(@RequestBody @Valid ChangeEmailDto changeEmailDto) {
        changeUserInfo.changeEmail(changeEmailDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/common/verify-change-email")
    public ResponseEntity<Void> verifyChangeEmail(@RequestParam String email, @RequestParam String code) {
        changeUserInfo.verifyChangeEmail(email, code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/user/change-phone-number")
    public ResponseEntity<Void> sendOTPToPhone(@RequestBody @Valid ChangePhoneNumberDto phoneNumberDto) {
        changeUserInfo.changePhoneNumber(phoneNumberDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/common/verify-change-phone-number")
    public ResponseEntity<Void> verifyChangePhoneNumber(@RequestParam String phoneNumber, @RequestParam String otp) {
        changeUserInfo.verifyChangePhoneNumber(phoneNumber, otp);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
