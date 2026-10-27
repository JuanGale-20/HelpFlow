package com.helpflow.tickets.infrastructure.adapter.in.rest.security;

import com.helpflow.tickets.domain.model.Role;
import com.helpflow.tickets.domain.model.User;
import com.helpflow.tickets.domain.valueobject.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.email())
                .claim("userId", user.id().value())
                .claim("role", user.role().name())
                .claim("name", user.name())
                .claim("document", user.document())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UserId extractUserId(String token) {
        Claims claims = extractClaims(token);
        Number userId = claims.get("userId", Number.class);
        return userId != null ? new UserId(userId.longValue()) : null;
    }

    public Role extractRole(String token) {
        Claims claims = extractClaims(token);
        String roleStr = claims.get("role", String.class);
        return roleStr != null ? Role.valueOf(roleStr) : null;
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
}
