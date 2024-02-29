package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.payload.request.ChangeEmailDto;
import net.sparkminds.librarymanagement.payload.request.ChangePassDto;
import net.sparkminds.librarymanagement.payload.request.ChangePhoneNumberDto;
import net.sparkminds.librarymanagement.payload.request.ResetPassDto;

public interface ChangeUserInfo {
    void resetPassword(ResetPassDto resetPassDto);

    void verifyResetPassword(String email, String password);

    void changePassword(ChangePassDto changePassDto);

    void changeEmail(ChangeEmailDto changeEmailDto);

    void verifyChangeEmail(String email, String token);

    void changePhoneNumber(ChangePhoneNumberDto phoneNumberDto);

    void verifyChangePhoneNumber(String newPhoneNumber, String otp);
}
