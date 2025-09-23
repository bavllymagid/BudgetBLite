package com.budget.b.lite.controllers;

import com.budget.b.lite.dto.LoginResponse;
import com.budget.b.lite.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
public class RefreshController {

    private final AuthService authService;

    public RefreshController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh() {
        LoginResponse response = authService.renewToken();
        return ResponseEntity.ok(response);
    }
}
