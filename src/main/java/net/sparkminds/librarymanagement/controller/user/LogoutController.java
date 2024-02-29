package net.sparkminds.librarymanagement.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.service.LogoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class LogoutController {
    private final LogoutService logoutService;

    /**
     * logout
     *
     * @return void
     */
    @Operation(summary = "Logout", description = "Logout", tags = {"Logout function"})
    @PostMapping("/logout")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        logoutService.logout(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
