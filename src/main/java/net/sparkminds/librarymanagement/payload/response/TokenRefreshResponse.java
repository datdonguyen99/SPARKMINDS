package net.sparkminds.librarymanagement.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenRefreshResponse {
    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";
}
