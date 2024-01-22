package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.entity.RefreshToken;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.payload.response.JwtResponse;
import net.sparkminds.librarymanagement.payload.response.TokenRefreshResponse;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.security.JwtTokenProvider;
import net.sparkminds.librarymanagement.service.AuthService;
import net.sparkminds.librarymanagement.service.RefreshTokenService;
import net.sparkminds.librarymanagement.utils.Status;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final AccountRepository accountRepository;

    private final AccountServiceImpl accountService;

    private final RefreshTokenService refreshTokenService;


    @Override
    public JwtResponse login(LoginDto loginDto) {
        Account account = accountRepository.findByEmail(loginDto.getEmail());
        if (account == null) {
            throw new ResourceNotFoundException("Incorrect email", "email.email-not-existed");
        }

        if (!account.getStatus().equals(Status.ACTIVE)) {
            throw new ResourceInvalidException("Please verify your account before login", "account.account-not-activated");
        }

        if (!accountService.isPasswordCorrect(loginDto.getPassword(), account)) {
            throw new ResourceInvalidException("Incorrect password", "password.password-not-correct");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();

        String accessToken = tokenProvider.generateJwtTokenFromEmail(customAccount.getAccount().getEmail());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(customAccount.getAccount().getId());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(customAccount.getAccount().getId())
                .username(customAccount.getAccount().getUsername())
                .email(customAccount.getAccount().getEmail())
                .roles(customAccount.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshDto tokenRefreshDto) {
        String newAccessToken = refreshTokenService.findByToken(tokenRefreshDto.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getAccount)
                .map(account -> tokenProvider.generateJwtTokenFromEmail(account.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token is not in database", "refreshToken.refreshToken-not-existed"));

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(tokenRefreshDto.getRefreshToken())
                .build();
    }
}
