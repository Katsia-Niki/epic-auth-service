package by.nikiforova.epic_auth_service.service;

import by.nikiforova.epic_auth_service.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;

    private final long accessTtl;

    private final long refreshTtl;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl-ms}") long accessTtl,
            @Value("${jwt.refresh-ttl-ms}") long refreshTtl) {
        this.secret = secret;
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String generateAccessToken(Long userId, Role role, String login) {
        return createToken(userId, role, login, accessTtl);
    }

    public String generateRefreshToken(Long userId, Role role, String login) {
        return createToken(userId, role, login, refreshTtl);
    }

    private String createToken(Long userId, Role role, String login, long ttl) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(ttl);

        return Jwts.builder()
                .subject(login)
                .claims(Map.of(
                        "userId", userId,
                        "role", role.name()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try{
            parseToken(token);
            return true;
        } catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
