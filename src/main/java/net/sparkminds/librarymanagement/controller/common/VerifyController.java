package net.sparkminds.librarymanagement.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.service.ChangeUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/common")
@RequiredArgsConstructor
public class VerifyController {
    private final ChangeUserInfo changeUserInfo;

    @Operation(summary = "Verify change email", description = "A link will be sent to email to verify when user change email", tags = {"Verify functions"})
    @GetMapping("/verify/change-email")
    public ResponseEntity<Void> verifyChangeEmail(@RequestParam String email, @RequestParam String code) {
        changeUserInfo.verifyChangeEmail(email, code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Verify reset password", description = "A link will be sent to email to verify when user reset password", tags = {"Verify functions"})
    @GetMapping("/verify/reset-password")
    public ResponseEntity<Void> verifyResetPassword(@RequestParam String email, @RequestParam String password) {
        changeUserInfo.verifyResetPassword(email, password);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
