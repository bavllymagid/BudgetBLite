package com.budget.b.lite.services;

import com.budget.b.lite.dto.LoginRequest;
import com.budget.b.lite.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {
    private final RestTemplate restTemplate;

    @Value("${user.auth}")
    private String AUTH_URL;

    public AuthService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public LoginResponse login(LoginRequest request){
        return restTemplate.postForObject(
                AUTH_URL+"/hello",
                request,
                LoginResponse.class
        );
    }
}
