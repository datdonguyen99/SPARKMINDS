package net.sparkminds.librarymanagement.controller.common;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.request.ResetPassDto;
import net.sparkminds.librarymanagement.service.ChangeUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/common")
@RequiredArgsConstructor
public class ResetInfoController {
    private final ChangeUserInfo changeUserInfo;

    /**
     * @param resetPassDto contain email and new password
     * @return
     */
    @Operation(summary = "Reset password", description = "Reset password", tags = {"Reset password function"})
    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPass(@RequestBody @Valid ResetPassDto resetPassDto) {
        changeUserInfo.resetPassword(resetPassDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
