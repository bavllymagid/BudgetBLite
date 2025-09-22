package com.budget.b.lite.controllers;

import com.budget.b.lite.dto.LoginRequest;
import com.budget.b.lite.dto.LoginResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @PostMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return new LoginResponse(request.username(), "asfasdfsdfsd");
    }

    @GetMapping("validate-token")
    public boolean validate(@RequestParam String token){
        return false;
    }
}
