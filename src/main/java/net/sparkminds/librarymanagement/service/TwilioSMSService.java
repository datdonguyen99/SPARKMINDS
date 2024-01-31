package net.sparkminds.librarymanagement.service;

public interface TwilioSMSService {
    void sendSMSOtp(String to, String otp);
}
