package com.budget.b.lite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/gateway")
public class AuthController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/hello")
    String hello(){
        String url = "http://AUTH-SERVICE/hello";
        return restTemplate.getForObject(url, String.class);
    }
}
