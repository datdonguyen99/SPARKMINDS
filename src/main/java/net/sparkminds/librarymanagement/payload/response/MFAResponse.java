package net.sparkminds.librarymanagement.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MFAResponse {
    private String secretKey;

    private String qrCodeUrl;
}
