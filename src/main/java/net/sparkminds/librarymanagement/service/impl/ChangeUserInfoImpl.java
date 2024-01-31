package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.ChangeEmailDto;
import net.sparkminds.librarymanagement.payload.request.ChangePassDto;
import net.sparkminds.librarymanagement.payload.request.ChangePhoneNumberDto;
import net.sparkminds.librarymanagement.payload.request.ResetPassDto;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.repository.VerificationOtpRepository;
import net.sparkminds.librarymanagement.repository.VerificationTokenRepository;
import net.sparkminds.librarymanagement.service.ChangeUserInfo;
import net.sparkminds.librarymanagement.service.MailSenderService;
import net.sparkminds.librarymanagement.service.TwilioSMSService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static net.sparkminds.librarymanagement.utils.AppConstants.SITE_URL;

@Service
@RequiredArgsConstructor
public class ChangeUserInfoImpl implements ChangeUserInfo {
    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailSenderService mailSenderService;

    private final VerificationTokenRepository tokenRepository;

    private final VerificationOtpRepository otpRepository;

    private final TwilioSMSService twilioSMSService;

    private Random random = new Random();

    private static String decimalFormat = "000000";

    @Override
    public void resetPassword(ResetPassDto resetPassDto) {
        Account account = accountRepository.findByEmail(resetPassDto.getEmail());
        if (account == null) {
            throw new ResourceNotFoundException("Email not found in the system", "account.email.email-not-found");
        }

        account.setPassword(passwordEncoder.encode(resetPassDto.getPassword()));
        accountRepository.save(account);

        mailSenderService.sendEmailToResetPassword(resetPassDto.getEmail(), resetPassDto.getPassword());
    }

    @Override
    public void changePassword(ChangePassDto changePassDto) {
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new ResourceNotFoundException("Account not found with email " + email, "account.account-not-found");
        }

        if (passwordEncoder.matches(changePassDto.getNewPassword(), account.getPassword())) {
            throw new ResourceInvalidException("Please type password same password currently used", "account.password.password-not-valid");
        }

        account.setPassword(passwordEncoder.encode(changePassDto.getNewPassword()));
        accountRepository.save(account);
    }

    @Override
    public void changeEmail(ChangeEmailDto changeEmailDto) {
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new ResourceNotFoundException("Account not found with email " + email, "account.account-not-found");
        }

        // create and save token corresponding to account in db
        VerificationToken vToken = new VerificationToken(account, UUID.randomUUID().toString());
        tokenRepository.save(vToken);

        // send email
        mailSenderService.sendEmailToChangeEmail(email, changeEmailDto.getEmail(), SITE_URL);
    }

    @Override
    public void verifyChangeEmail(String email, String token) {
        VerificationToken vToken = tokenRepository.findVerificationTokenByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("token not found", "token.token-not-found"));

        if (vToken.getExpiryDate().before(new Date())) {
            tokenRepository.deleteById(vToken.getId());
            throw new ResourceInvalidException("Token expired", "token.token-expired");
        }

        Account account = vToken.getAccount();
        account.setEmail(email);
        accountRepository.save(account);

        tokenRepository.deleteById(vToken.getId());
    }

    @Override
    public void changePhoneNumber(ChangePhoneNumberDto phoneNumberDto) {
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new ResourceNotFoundException("Account not found with email " + email, "account.account-not-found");
        }

        // create and save otp corresponding to account in db
        VerificationOtp vOtp = new VerificationOtp(account, new DecimalFormat(decimalFormat).format(random.nextInt(999999)));
        otpRepository.save(vOtp);

         // send to twilioSMS
//        twilioSMSService.sendSMSOtp(phoneNumberDto.getPhoneNumber(), vOtp.getOtp());
    }

    @Override
    public void verifyChangePhoneNumber(String newPhoneNumber, String otp) {
        VerificationOtp vOtp = otpRepository.findVerificationOtpByOtp(otp)
                .orElseThrow(() -> new ResourceNotFoundException("otp not found", "otp.otp-not-found"));

        if (vOtp.getExpiryDate().before(new Date())) {
            otpRepository.deleteById(vOtp.getId());
            throw new ResourceInvalidException("OTP expired", "otp.otp-expired");
        }

        User user = (User) vOtp.getAccount();
        user.setPhoneNumber(newPhoneNumber);
        userRepository.save(user);

        otpRepository.deleteById(vOtp.getId());
    }

}
