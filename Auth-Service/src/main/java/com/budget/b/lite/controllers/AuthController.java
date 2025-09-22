package com.budget.b.lite.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("hello")
    String hello(){
        return "hello discovered";
    }
}
