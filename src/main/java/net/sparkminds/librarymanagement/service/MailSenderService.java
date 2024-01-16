package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.entity.Account;

public interface MailSenderService {
    void sendVerificationEmail(Account account, String siteURL);
}
