package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnableMfaDto {
    @NotEmpty(message = "secret key field is required")
    private String secretKey;

    @NotEmpty(message = "OTP code field is required")
    private String otp;

    @NotEmpty(message = "Email field is required")
    @Size(min = 3, message = "Email must be at least 3 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    private String email;
}
