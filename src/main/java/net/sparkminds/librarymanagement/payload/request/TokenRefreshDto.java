package net.sparkminds.librarymanagement.payload.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshDto {
    @NotEmpty(message = "refreshToken field is required")
    private String refreshToken;
}
