package net.sparkminds.librarymanagement.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;

    @NotEmpty(message = "Email field is required")
    @Size(min = 3, message = "Email must be at least 3 characters")
    @Email(message = "Email must be valid")
    private String email;

    @NotEmpty(message = "Password field is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;
}
