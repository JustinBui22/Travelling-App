package com.example.travelingapp.controller;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.ResponseBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/public/api/users/")
public interface UserController {
    @PostMapping("/register/phone")
    ResponseEntity<ResponseBody<Object>> createNewUserByPhoneNumber(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/register/email")
    ResponseEntity<ResponseBody<Object>> createNewUserByEmail(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/login")
    ResponseEntity<ResponseBody<Object>> login(@Valid @RequestBody LoginDTO loginRequest);
}