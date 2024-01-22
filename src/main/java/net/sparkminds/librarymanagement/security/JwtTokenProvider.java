package net.sparkminds.librarymanagement.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import net.sparkminds.librarymanagement.exception.ResourceInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${sparkminds.app.jwtSecret}")
    private String jwtSecret;

    @Value("${sparkminds.app.jwtExpirationMs}")
    private Long jwtExpirationMs;        // milliseconds

    // generate JWT token from username
    public String generateJwtTokenFromEmail(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(LocalDateTime.now().plus(jwtExpirationMs, ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // get username from Jwt token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            return true;
        } catch (MalformedJwtException e) {
            throw new ResourceInvalidException(e.getMessage(), "JWT.token-invalid");
        } catch (ExpiredJwtException e) {
            throw new ResourceInvalidException(e.getMessage(), "JWT.token-expired");
        } catch (UnsupportedJwtException e) {
            throw new ResourceInvalidException(e.getMessage(), "JWT.token-unsupported");
        } catch (IllegalArgumentException e) {
            throw new ResourceInvalidException(e.getMessage(), "JWT.claim-string-empty");
        }
    }
}