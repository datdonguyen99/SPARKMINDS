package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.entity.User;

public interface MailSenderService {
    void sendVerificationEmail(User user, String siteURL);
}
