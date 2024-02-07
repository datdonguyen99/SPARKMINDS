package net.sparkminds.librarymanagement.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Session;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.repository.SessionRepository;
import net.sparkminds.librarymanagement.security.JwtTokenProvider;
import net.sparkminds.librarymanagement.service.LogoutService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    private final JwtTokenProvider tokenProvider;

    private final SessionRepository sessionRepository;

    @Override
    public void logout(HttpServletRequest request) {
        String token = null;
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        String jti = tokenProvider.getJtiFromRefreshToken(token);
        Session session = sessionRepository.findByJti(jti).orElseThrow(() -> new ResourceNotFoundException("Session not found", "session.token.token-not-found"));
        session.setActive(false);
        sessionRepository.save(session);

        SecurityContextHolder.clearContext();
    }
}
