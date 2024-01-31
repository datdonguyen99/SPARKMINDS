package net.sparkminds.librarymanagement.service.impl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.exception.ResourceUnauthorizedException;
import net.sparkminds.librarymanagement.payload.response.MFAResponse;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.service.MfaService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    private final AccountRepository accountRepository;

    @Override
    public MFAResponse generateMFACode() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL("Authenticator", "datdonguyen@gmail.com", key);

        return MFAResponse.builder()
                .secretKey(key.getKey())
                .qrCodeUrl(otpAuthURL)
                .build();
    }

    @Override
    public void enableMFACode(String secretKey, String otp) {
        CustomAccount customAccount = (CustomAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = customAccount.getAccount().getEmail();
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            throw new ResourceNotFoundException("Account not found with email " + email, "account.account-not-found");
        }

        if (verifyOtpCode(secretKey, otp)) {
            account.setSecretKey(secretKey);
            account.setEnableMFA(true);
            accountRepository.save(account);
        }
    }

    public boolean verifyOtpCode(String secretKey, String otpCode) {
        if (googleAuthenticator.authorize(secretKey, Integer.parseInt(otpCode))) {
            return true;
        } else {
            throw new ResourceUnauthorizedException("Invalid OTP", "2fa.otp-not-valid");
        }
    }
}
