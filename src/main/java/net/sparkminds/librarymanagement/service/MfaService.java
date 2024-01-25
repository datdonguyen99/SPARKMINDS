package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.response.MFAResponse;

public interface MfaService {
    MFAResponse generateMFACode();

    void enableMFACode(String secretKey, String otp, String email);
}
