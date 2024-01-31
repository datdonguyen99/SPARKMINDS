package net.sparkminds.librarymanagement.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${sparkminds.app.jwtSecret}")
    private String jwtSecret;

    @Value("${sparkminds.app.jwtExpirationMs}")
    private Long jwtExpirationMs;        // milliseconds

    @Value("${sparkminds.app.jwtRefreshExpirationMs}")
    private Long jwtRefreshExpirationMs;        // milliseconds

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // generate JWT token from email
    public String generateJwtTokenFromEmail(String email, String jti) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(LocalDateTime.now().plus(jwtExpirationMs, ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toInstant()))
                .setId(jti)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // generate JWT token from email
    public String generateJwtRefreshTokenFromEmail(String email, String jti) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(LocalDateTime.now().plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS).atZone(ZoneId.systemDefault()).toInstant()))
                .setId(jti)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // get email from Jwt token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // parse jti from jwt token
    public String getJtiFromRefreshToken(String authToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(authToken)
                .getBody()
                .getId();
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
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}