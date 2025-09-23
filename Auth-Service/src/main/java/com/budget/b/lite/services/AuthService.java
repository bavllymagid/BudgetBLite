package com.budget.b.lite.services;


import com.budget.b.lite.dto.LoginRequest;
import com.budget.b.lite.dto.LoginResponse;
import com.budget.b.lite.dto.RegisterRequest;
import com.budget.b.lite.entities.User;
import com.budget.b.lite.repositories.UserRepository;
import com.budget.b.lite.exception_handling.custom_exceptions.InvalidCredentialsException;
import com.budget.b.lite.exception_handling.custom_exceptions.UserAlreadyExistsException;
import com.budget.b.lite.exception_handling.custom_exceptions.UserNotFoundException;
import com.budget.b.lite.utils.jwt.JWTUtils;
import com.budget.b.lite.utils.user_config.UserInfo;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JWTUtils jwtUtils,
            AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public String register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new UserAlreadyExistsException("User already Exists with the same email");
        }


        User user = new User(request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                "");

        userRepository.save(user);

        return "User Registered successfully!";
    }

    public LoginResponse login(LoginRequest user){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.email(), user.password())
            );
            UserInfo userDetails = (UserInfo) authentication.getPrincipal();
            Optional<User> updateUser = userRepository.findByEmail(userDetails.getEmail());
            String secret = generateSecret();

            if(updateUser.isPresent()){
                updateUser.get().setJwtSecret(secret);
                userRepository.save(updateUser.get());
            }else throw new UserNotFoundException("user Not found with this credentials");
            userDetails.setJwtSecret(secret);
            String jwt = jwtUtils.generateToken(userDetails, Duration.ofHours(1));

            return new LoginResponse(jwt, refreshTokenService.createRefreshToken(userDetails.getEmail()).getToken(), userDetails.getEmail());
         }catch (BadCredentialsException e){
            throw new InvalidCredentialsException("Invalid credentials: " + e);
        }
    }

    public LoginResponse renewToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo) authentication.getPrincipal();

        Optional<User> updateUser = userRepository.findByEmail(user.getEmail());
        if(updateUser.isPresent()){
            User newUser = updateUser.get();
            String secret = generateSecret();
            newUser.setJwtSecret(secret);
            user.setJwtSecret(secret);
            String jwt = jwtUtils.generateToken(user, Duration.ofHours(1));
            userRepository.save(newUser);
            return new LoginResponse(jwt, refreshTokenService.findByUser(newUser).get().getToken(), user.getEmail());
        }

        throw new UserNotFoundException("user Not found with this credentials");
    }

    public void deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo) authentication.getPrincipal();
        if(user == null) throw new UserNotFoundException("user Not found can't delete");
        userRepository.deleteByEmail(user.getEmail());
    }

    private String generateSecret(){
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(key.getEncoded());
    }

    public void logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo) authentication.getPrincipal();
        if(user == null) throw new UserNotFoundException("user Not found can't logout");

        Optional<User> updateUser = userRepository.findByEmail(user.getEmail());
        if(updateUser.isPresent()){
            User newUser = updateUser.get();
            newUser.setJwtSecret("");
            userRepository.save(newUser);
            refreshTokenService.deleteByEmail(user.getEmail());
        }
    }
}
