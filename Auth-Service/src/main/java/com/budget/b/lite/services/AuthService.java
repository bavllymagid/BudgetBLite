package com.budget.b.lite.services;


import com.budget.b.lite.dto.LoginRequest;
import com.budget.b.lite.dto.LoginResponse;
import com.budget.b.lite.dto.RegisterRequest;
import com.budget.b.lite.entities.User;
import com.budget.b.lite.repositories.UserRepository;
import com.budget.b.lite.utils.exception.custom_exceptions.InvalidCredentialsException;
import com.budget.b.lite.utils.jwt.JWTUtils;
import com.budget.b.lite.utils.user_config.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    public String register(RegisterRequest request){
        if(repository.existsByEmail(request.email())){
            return "Email is already taken";
        }

        User user = new User(request.username(),
                request.email(),
                passwordEncoder.encode(request.password()));

        repository.save(user);

        return "User Registered successfully!";
    }

    public LoginResponse login(LoginRequest user){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.username(), user.password())
            );

            UserInfo userDetails = (UserInfo) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails, Duration.ofHours(1));

            return new LoginResponse(jwt, userDetails.getUsername());
         }catch (BadCredentialsException e){
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
}
