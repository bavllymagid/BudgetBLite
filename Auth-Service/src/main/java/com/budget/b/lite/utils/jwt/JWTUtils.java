package com.budget.b.lite.utils.jwt;

import com.budget.b.lite.utils.user_config.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils {
    private Duration expiration;

    private SecretKey getSignInKey(String userSecret) {
        byte[] keyBytes = Decoders.BASE64.decode(userSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(String username, String userSecret, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration.toMillis()))
                .signWith(getSignInKey(userSecret))
                .compact();
    }

    public String generateToken(UserInfo userInfo, Duration expiration) {
        this.expiration = expiration;
        return generateToken(userInfo.getEmail(), userInfo.getJwtSecret(), Map.of());
    }

    public <T> T extractClaim(String token, String userSecret, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token, userSecret));
    }

    public String extractEmail(String token, String userSecret) {
        return extractClaim(token, userSecret, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String email, String userSecret) {
        String extractEmail = extractEmail(token, userSecret);
        return extractEmail.equals(email) && !isTokenExpired(token, userSecret);
    }

    private boolean isTokenExpired(String token, String userSecret) {
        Date expirationDate = extractClaim(token, userSecret, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    private Claims extractAllClaims(String token, String userSecret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(userSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmailFromPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<?,?> payload = mapper.readValue(payloadJson, Map.class);
            return (String) payload.get("sub");
        } catch (Exception e) {
            return null;
        }
    }
}
