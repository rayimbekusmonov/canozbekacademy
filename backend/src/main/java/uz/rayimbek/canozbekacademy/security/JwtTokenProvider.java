package uz.rayimbek.canozbekacademy.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long jwtExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration}") long jwtExpirationMs,
            @Value("${app.jwt.refresh-expiration}") long refreshExpirationMs
    ) {
        // Yangi versiyada kalitni yaratish
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getId(), userPrincipal.getEmail(), false);
    }

    public String generateToken(Long userId, String email) {
        return generateToken(userId, email, false);
    }

    public String generateRefreshToken(Long userId, String email) {
        return generateToken(userId, email, true);
    }

    private String generateToken(Long userId, String email, boolean isRefresh) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (isRefresh ? refreshExpirationMs : jwtExpirationMs));

        // 0.12.x versiyasida builder metodlari o'zgargan:
        return Jwts.builder()
                .subject(String.valueOf(userId)) // setSubject -> subject
                .claim("email", email)
                .claim("type", isRefresh ? "refresh" : "access")
                .issuedAt(now) // setIssuedAt -> issuedAt
                .expiration(expiryDate) // setExpiration -> expiration
                .signWith(key) // Algoritm avtomatik aniqlanadi
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        // parserBuilder() o'rniga parser(), setSigningKey o'rniga verifyWith()
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token) // parseClaimsJws -> parseSignedClaims
                .getPayload(); // getBody -> getPayload

        return Long.parseLong(claims.getSubject());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("JWT xatosi: {}", ex.getMessage());
        }
        return false;
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }
}