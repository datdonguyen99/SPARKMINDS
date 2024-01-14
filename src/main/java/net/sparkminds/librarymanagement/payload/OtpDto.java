package net.sparkminds.librarymanagement.payload;

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
public class OtpDto {
    @NotEmpty(message = "OTP field is required")
    @Size(min = 6, max = 6, message = "OTP must contain 6 digits")
    private String otp;
}
