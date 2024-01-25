package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.entity.Session;
import net.sparkminds.librarymanagement.exception.ResourceForbiddenException;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.exception.ResourceUnauthorizedException;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.payload.response.JwtResponse;
import net.sparkminds.librarymanagement.payload.response.TokenRefreshResponse;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.repository.SessionRepository;
import net.sparkminds.librarymanagement.security.JwtTokenProvider;
import net.sparkminds.librarymanagement.service.AuthService;
import net.sparkminds.librarymanagement.utils.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${sparkminds.app.jwtRefreshExpirationMs}")
    private Long jwtRefreshExpirationMs;        // milliseconds

    @Value("${sparkminds.app.blockedStatusExpirationMs}")
    private Long blockedStatusExpirationMs;        // milliseconds

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final AccountRepository accountRepository;

    private final AccountServiceImpl accountService;

    private final SessionRepository sessionRepository;

    private final MfaServiceImpl mfaService;


    @Override
    public JwtResponse login(LoginDto loginDto) {
        Account account = accountRepository.findByEmail(loginDto.getEmail());
        if (account == null) {
            throw new ResourceNotFoundException("Incorrect email", "email.email-not-existed");
        }

        if (account.getStatus().equals(Status.BLOCKED)) {
            if (LocalDateTime.now().isBefore(account.getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS))) {
                throw new ResourceInvalidException("Please login after " + account.getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS), "account.account-blocked");
            } else {
                account.setStatus(Status.ACTIVE);
                accountRepository.save(account);
            }
        }

        if (!account.getStatus().equals(Status.ACTIVE)) {
            throw new ResourceInvalidException("Please verify your account before login", "account.account-not-activated");
        }

        if (!accountService.isPasswordCorrect(loginDto.getPassword(), account)) {
            account.setLoginCount(account.getLoginCount() + 1);
            accountRepository.save(account);
            throw new ResourceInvalidException("Incorrect password", "password.password-not-correct");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();

        customAccount.getAccount().setLatestLoginTime(LocalDateTime.now());
        if (customAccount.getAccount().getLoginCount() >= 3) {
            customAccount.getAccount().setLoginCount(0);
            customAccount.getAccount().setStatus(Status.BLOCKED);
        } else {
            customAccount.getAccount().setLoginCount(customAccount.getAccount().getLoginCount() + 1);
        }
        accountRepository.save(customAccount.getAccount());

        if (customAccount.getAccount().isEnableMFA()) {
            if (loginDto.getOtp().equals("")) {
                throw new ResourceUnauthorizedException("OTP field is empty", "2fa.otp-not-found");
            }
            mfaService.verifyOtpCode(customAccount.getAccount().getSecretKey(), loginDto.getOtp());
        }

        String jti = UUID.randomUUID().toString().replace("-", "");
        Session session = Session.builder()
                .jti(jti)
                .expireDate(LocalDateTime.now().plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS))
                .account(customAccount.getAccount())
                .build();
        sessionRepository.save(session);

        String accessToken = tokenProvider.generateJwtTokenFromEmail(customAccount.getAccount().getEmail(), jti);
        String refreshToken = tokenProvider.generateJwtRefreshTokenFromEmail(customAccount.getAccount().getEmail(), jti);

        customAccount.getAccount().setLoginCount(0);
        customAccount.getAccount().setStatus(Status.ACTIVE);
        accountRepository.save(customAccount.getAccount());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(customAccount.getAccount().getId())
                .username(customAccount.getAccount().getUsername())
                .email(customAccount.getAccount().getEmail())
                .roles(customAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshDto tokenRefreshDto) {
        String jti = tokenProvider.getJtiFromRefreshToken(tokenRefreshDto.getRefreshToken());

        Session session = sessionRepository.findByJti(jti)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found", "refreshToken.refreshToken-not-existed"));

        if (session.getExpireDate().isBefore(LocalDateTime.now())) {
            session.setActive(false);
            sessionRepository.save(session);

            throw new ResourceForbiddenException("Refresh token was expired", "refreshToken.refreshToken-expired");
        }

        String email = tokenProvider.getEmailFromJwtToken(tokenRefreshDto.getRefreshToken());
        String newRefreshToken = tokenProvider.generateJwtRefreshTokenFromEmail(email, jti);

        session.setExpireDate(LocalDateTime.now().plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS));
        sessionRepository.save(session);

        return TokenRefreshResponse.builder()
                .accessToken(tokenRefreshDto.getRefreshToken())
                .refreshToken(newRefreshToken)
                .build();
    }
}
