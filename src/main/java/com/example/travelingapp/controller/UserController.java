package com.example.travelingapp.controller;

import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.ResponseBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/public/api/users/")
public interface UserController {
    @PostMapping("/register/phone")
    ResponseEntity<ResponseBody<String>> createNewUserByPhoneNumber(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/register/email")
    ResponseEntity<ResponseBody<String>> createNewUserByEmail(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/login")
    ResponseEntity<ResponseBody<String>> login(@RequestBody UserDTO loginRequest);
}