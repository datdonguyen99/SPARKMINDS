package net.sparkminds.librarymanagement.service.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.service.TwilioSMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwilioSMSServiceImpl implements TwilioSMSService {
    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String phoneNumber;

    @Override
    public void sendSMSOtp(String to, String otp) {
        Twilio.init(accountSid, authToken);

        // Your Twilio phone number
        PhoneNumber fromNumber = new PhoneNumber(phoneNumber);

        // The recipient's phone number
        PhoneNumber toNumber = new PhoneNumber(to);

        // Message content
        String messageBody = "Your OTP is: " + otp;

        // Create and send SMS
        Message message = Message.creator(toNumber, fromNumber, messageBody).create();
    }
}
