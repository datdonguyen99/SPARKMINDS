package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.OtpDto;
import net.sparkminds.librarymanagement.payload.request.RegisterDto;
import net.sparkminds.librarymanagement.payload.request.ResendDto;

public interface RegisterService {
    void register(RegisterDto registerDTO);

    void verifyToken(String token);

    void resendToken(ResendDto resendDto);

    void verifyOtp(OtpDto otpDto);

    void resendOTP(ResendDto resendDto);
}
