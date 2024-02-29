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
//    @PreAuthorize("hasRole('USER')")
//    @Secured("ROLE_USER")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/admin/a")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Secured("ROLE_ADMIN")
    public String adminAccess() {
        return "Admin Content.";
    }
}
