package com.budget.b.lite.services;

import com.budget.b.lite.entities.RefreshToken;
import com.budget.b.lite.entities.User;
import com.budget.b.lite.repositories.RefreshTokenRepository;
import com.budget.b.lite.utils.jwt.JWTUtils;
import com.budget.b.lite.utils.user_config.UserInfo;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public RefreshTokenService(RefreshTokenRepository repository,
                               JWTUtils jwtUtils,
                               UserDetailsService userDetailsService){
        this.repository = repository;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    public RefreshToken createRefreshToken(String email) {
        Duration duration = Duration.ofDays(3);
        UserInfo user = (UserInfo) userDetailsService.loadUserByUsername(email);
        deleteByEmail(email); // if exists
        String secret = generateSecret();
        String token = jwtUtils.generateToken(secret, user.getEmail(), duration);
        RefreshToken refreshToken = new RefreshToken(user.getUser(), token, secret, Timestamp.from(Instant.now().plus(duration)));
        return repository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    public Optional<RefreshToken> findByUser(User user) {
        return repository.findByUser(user);
    }


    public void deleteByEmail(String email) {
        repository.deleteByUserEmail(email);
    }

    private String generateSecret(){
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(key.getEncoded());
    }

}
