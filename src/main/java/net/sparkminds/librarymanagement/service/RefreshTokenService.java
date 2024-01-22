package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long accountId);

    RefreshToken verifyExpiration(RefreshToken token);
}
