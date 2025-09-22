package com.budget.b.lite.controllers;

import com.budget.b.lite.dto.LoginRequest;
import com.budget.b.lite.dto.LoginResponse;
import com.budget.b.lite.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest login){
        return ResponseEntity.ok(service.login(login));
    }
}
