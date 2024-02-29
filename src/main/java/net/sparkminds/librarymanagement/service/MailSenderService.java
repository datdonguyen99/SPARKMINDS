package net.sparkminds.librarymanagement.service;

public interface MailSenderService {
    void sendVerificationEmail(String email, String siteURL);

    void sendEmailToVerifyResetPassword(String oldEmail, String newPassword);

    void sendEmailToChangeEmail(String oldEmail, String newEmail, String token);

    void sendEmailToUserBorrowedBook(String email, String imgPath);
}
