package com.example.travelingapp.controller;

import com.example.travelingapp.util.HttpStatusCode;
import com.example.travelingapp.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users/")
public interface UserController {
    @PostMapping("/register")
    ResponseEntity<HttpStatusCode> createNewUser(@RequestBody UserDTO registerRequest);

    @PostMapping("/login")
    ResponseEntity<HttpStatusCode> login(@RequestBody UserDTO loginRequest);
}