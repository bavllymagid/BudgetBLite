package com.budget.b.lite.controllers;

import com.budget.b.lite.dto.LoginResponse;
import com.budget.b.lite.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/acc")
public class AccountController {

    private final AuthService authService;

    public AccountController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<LoginResponse> refresh() {
        LoginResponse response = authService.renewToken();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delete")
    public ResponseEntity<String> delete(){
        authService.deleteUser();
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/token/validate")
    public ResponseEntity<Boolean> validate(){
        return ResponseEntity.ok(true);
    }
}
