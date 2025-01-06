package com.example.travelingapp.controller;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.response_template.ResponseBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/public/api/users/")
public interface UserController {
    @PostMapping("/register/phone")
    ResponseEntity<ResponseBody<Object>> createNewUser(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/register/email")
    ResponseEntity<ResponseBody<Object>> createNewUserByEmail(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/login")
    ResponseEntity<ResponseBody<Object>> login(@Valid @RequestBody LoginDTO loginRequest);

    @PostMapping("/logout")
    ResponseEntity<ResponseBody<Object>> logout(@Valid @RequestBody LoginDTO loginRequest);

    @GetMapping("/test")
    ResponseEntity<ResponseBody<Object>> test(@RequestParam(name = "password") String password);
}