package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.RefreshToken;
import net.sparkminds.librarymanagement.exception.ResourceForbiddenException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.repository.RefreshTokenRepository;
import net.sparkminds.librarymanagement.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${sparkminds.app.jwtRefreshExpirationMs}")
    private Long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AccountRepository accountRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long accountId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .account(accountRepository.findById(accountId).orElseThrow(() ->
                        new ResourceNotFoundException("Account not found with id " + accountId, "account.account-not-found"))
                )
                .token(UUID.randomUUID().toString())
                .expireDate(LocalDateTime.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS))
                .build();
        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token.getToken()).orElseThrow(() -> new ResourceNotFoundException("Not found refresh token", "refreshToken.refreshToken-not-found"));

        if (refreshToken.getExpireDate().isBefore(LocalDateTime.now())) {
            refreshToken.setUsed(true);
            refreshTokenRepository.save(refreshToken);

            throw new ResourceForbiddenException("Refresh token was expired", "refreshToken.refreshToken-expired");
        }

        return token;
    }
}
