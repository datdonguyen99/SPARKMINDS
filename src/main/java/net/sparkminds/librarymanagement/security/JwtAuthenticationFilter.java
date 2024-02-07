package net.sparkminds.librarymanagement.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Session;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import net.sparkminds.librarymanagement.exception.ResourceNotFoundException;
import net.sparkminds.librarymanagement.repository.SessionRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;

    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // get JWT token from http request
            String token = getTokenFromRequest(request);

            // validate token
            if (StringUtils.hasText(token)) {
                // check session isActive?
                if (isActiveSession(token)) {
                    // get email from token
                    String email = jwtTokenProvider.getEmailFromJwtToken(token);

                    // load the user associated with token
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new ResourceInvalidException("Token is invalid^^", "token.token-not-valid");
                }
            }
        } catch (ExpiredJwtException ex) {
            // Handle token expiration
            request.setAttribute("expired", ex.getMessage());
        } catch (MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException ex) {
            // Handle invalid token
            request.setAttribute("Invalid", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private boolean isActiveSession(String token) {
        String jti = jwtTokenProvider.getJtiFromRefreshToken(token);
        Session session = sessionRepository.findByJti(jti).orElseThrow(() -> new ResourceNotFoundException("Session not found", "session.token.token-not-found"));

        return session.isActive();
    }
}
