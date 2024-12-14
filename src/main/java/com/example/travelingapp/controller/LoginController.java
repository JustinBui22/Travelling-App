package com.example.travelingapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/welcome")
    public String welcomePage(Authentication authentication) {
        // Use the Authentication object to get user details
        return "welcome";
    }
}