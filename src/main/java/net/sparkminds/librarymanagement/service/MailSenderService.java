package net.sparkminds.librarymanagement.service;

public interface MailSenderService {
    void sendVerificationEmail(String email, String siteURL);

    void sendEmailToResetPassword(String email, String newPassword);

    void sendEmailToChangeEmail(String oldEmail, String newEmail, String siteURL);
}
