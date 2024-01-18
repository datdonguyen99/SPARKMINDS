package net.sparkminds.librarymanagement.service;

public interface MailSenderService {
    void sendVerificationEmail(String email, String siteURL);
}
