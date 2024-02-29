package net.sparkminds.librarymanagement.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.entity.Session;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
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

    @Value("${sparkminds.app.jwtSecret}")
    private String jwtSecret;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final AccountRepository accountRepository;

    private final SessionRepository sessionRepository;

    private final MfaServiceImpl mfaService;


    @Override
    public JwtResponse login(LoginDto loginDto) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
        } catch (AuthenticationException e) {
            Account account = accountRepository.findByEmail(loginDto.getEmail());
            if (account == null) {
                throw new ResourceInvalidException("Incorrect email or password", "login.invalid-credentials");
            }

            if (account.getStatus().equals(Status.BLOCKED)) {
                if (LocalDateTime.now().isBefore(account.getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS))) {
                    throw new ResourceInvalidException("Please login after " + account.getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS), "account.account-blocked");
                } else {
                    account.setStatus(Status.ACTIVE);
                    account.setLoginCount(account.getLoginCount() + 1);
                    account.setLatestLoginTime(LocalDateTime.now());
                    accountRepository.save(account);
                }
            } else {
                account.setLoginCount(account.getLoginCount() + 1);
                if (account.getLoginCount() >= 3) {
                    account.setLoginCount(0);
                    account.setStatus(Status.BLOCKED);
                    throw new ResourceInvalidException("Please login after " + account.getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS), "account.account-blocked");
                }
                account.setLatestLoginTime(LocalDateTime.now());
                accountRepository.save(account);
            }

            throw new ResourceInvalidException("Incorrect email or password", "login.invalid-credentials");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();

        if (customAccount.getAccount().getStatus().equals(Status.BLOCKED)) {
            if (LocalDateTime.now().isBefore(customAccount.getAccount().getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS))) {
                throw new ResourceInvalidException("Please login after " + customAccount.getAccount().getLatestLoginTime().plus(blockedStatusExpirationMs, ChronoUnit.MILLIS), "account.account-blocked");
            } else {
                customAccount.getAccount().setStatus(Status.ACTIVE);
                accountRepository.save(customAccount.getAccount());
            }
        }

        if (!customAccount.getAccount().getStatus().equals(Status.ACTIVE)) {
            throw new ResourceInvalidException("Please verify your account before login", "account.account-not-activated");
        }

        customAccount.getAccount().setLatestLoginTime(LocalDateTime.now());

        if (customAccount.getAccount().isEnableMFA()) {
            if (!loginDto.getOtp().matches("\\d{6}")) {
                throw new ResourceUnauthorizedException("Invalid OTP format", "account.otp.invalid-otp-format");
            }
            mfaService.verifyOtpCode(customAccount.getAccount().getSecretKey(), loginDto.getOtp());
        }

        customAccount.getAccount().setLoginCount(0);
        accountRepository.save(customAccount.getAccount());


        String jti = UUID.randomUUID().toString().replace("-", "");
        Session session = Session.builder()
                .jti(jti)
                .expireDate(LocalDateTime.now().plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS))
                .account(customAccount.getAccount())
                .build();
        sessionRepository.save(session);

        String accessToken = tokenProvider.generateJwtTokenFromEmail(customAccount.getAccount().getEmail(), jti);
        String refreshToken = tokenProvider.generateJwtRefreshTokenFromEmail(customAccount.getAccount().getEmail(), jti);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(customAccount.getAccount().getId())
                .username(customAccount.getAccount().getUsername())
                .email(customAccount.getAccount().getEmail())
                .roles(customAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshDto tokenRefreshDto) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(tokenRefreshDto.getRefreshToken())
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new ResourceUnauthorizedException("token invalid", "token.token-invalid");
        } catch (ExpiredJwtException e) {
            String jti = e.getClaims().getId();
            Session session = sessionRepository.findLatestActiveSessionByJti(jti)
                    .orElseThrow(() -> new ResourceNotFoundException("Token not found", "Token.token-not-existed"));
            session.setActive(false);
            sessionRepository.save(session);
            throw new ResourceUnauthorizedException("token expired", "token.token-expired");
        } catch (UnsupportedJwtException e) {
            throw new ResourceUnauthorizedException("token is unsupported", "token.token-unsupported");
        } catch (IllegalArgumentException e) {
            throw new ResourceUnauthorizedException("claims string is empty", "token.claim-string-empty");
        }

        if (!tokenProvider.isRefreshToken(tokenRefreshDto.getRefreshToken())) {
            throw new ResourceUnauthorizedException("token invalid", "token.token-invalid");
        }

        String jti = tokenProvider.getJtiFromRefreshToken(tokenRefreshDto.getRefreshToken());
        String email = tokenProvider.getEmailFromJwtToken(tokenRefreshDto.getRefreshToken());
        String newAccessToken = tokenProvider.generateJwtTokenFromEmail(email, jti);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(tokenRefreshDto.getRefreshToken())
                .build();
    }
}
