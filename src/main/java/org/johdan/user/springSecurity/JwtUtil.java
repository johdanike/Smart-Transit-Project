package org.johdan.user.springSecurity;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.johdan.user.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    /** Access tokens valid for 10 hours */
    private static final long ACCESS_TOKEN_EXP_MS  = 1000L * 60 * 60 * 10;

    /** Refresh tokens valid for 7 days */
    private static final long REFRESH_TOKEN_EXP_MS = 1000L * 60 * 60 * 24 * 7;

    public JwtUtil(@Value("${JWT-SECRET}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate an access token that includes the user's role.
     */
    public String generateAccessToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a refresh token (no role claim)—longer lived.
     */
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates any token (access or refresh) by signature and expiration.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    /** Extract subject (username/email) from token */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Extract role claim—only valid for access tokens */
    public Role getRoleFromToken(String token) {
        String role = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
        return Role.valueOf(role);
    }

    /** Expose refresh expiration so the service can compute expiryDate */
    public long getRefreshTokenExpiryMs() {
        return REFRESH_TOKEN_EXP_MS;
    }

}
