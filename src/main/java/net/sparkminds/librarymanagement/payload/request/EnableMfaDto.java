package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnableMfaDto {
    @NotEmpty(message = "secret key field is required")
    private String secretKey;

    @NotEmpty(message = "OTP code field is required")
    private String otp;
}
