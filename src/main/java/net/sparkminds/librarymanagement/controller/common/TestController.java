package net.sparkminds.librarymanagement.controller.common;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1")
public class TestController {
    @GetMapping("/user/u")
//    @Secured("ROLE_USER")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/admin/a")
//    @Secured("ROLE_ADMIN")
//    @PreAuthorize("hasRole('ROLE_USER')")
    public String adminAccess() {
        return "Admin Content.";
    }
}
