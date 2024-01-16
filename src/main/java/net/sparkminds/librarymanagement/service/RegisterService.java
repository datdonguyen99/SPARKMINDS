package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import net.sparkminds.librarymanagement.payload.OtpDto;
import net.sparkminds.librarymanagement.payload.RegisterDto;

public interface RegisterService {
    void register(RegisterDto registerDTO);

    void createVerificationTokenForUser(Account account, String token);

    VerificationToken generateNewVerificationToken(String token);

    String validateVerificationToken(String token);

    String verifyToken(String token);

    void createVerificationOtpForUser(Account account, String otp);

    VerificationOtp generateNewVerificationOtp(String otp);

    String validateVerificationOtp(String otp);

    String verifyOtp(OtpDto otpDto);
}
