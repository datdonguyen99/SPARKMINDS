package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterDto {
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters")
    private String username;

    /**
     * Standard RFC 5322
     * Characters (|) and apostrophes (') are not allowed because of SQL injection
     */
    @NotEmpty(message = "Email field is required")
    @Size(min = 3, message = "Email must be at least 3 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Password field is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+=])(?=.*\\d).*$", message = "Invalid password format")
    private String password;

    /**
     * Mã vùng của Viettel (32-39), VinaPhone (56, 58, 59),
     * MobiFone (70, 76-79), Vietnamobile (81-89), Gmobile (90-99).
     */
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-9]|9\\d)\\d{7}$", message = "Invalid phone number")
    private String phoneNumber;
}
