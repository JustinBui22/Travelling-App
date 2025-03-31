package com.example.travelingapp.controller;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.response_template.ResponseBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/public/api/users/")
public interface UserController {
    @PostMapping("/register/phone")
    ResponseEntity<ResponseBody<Object>> createNewUser(@Valid @RequestBody UserDTO registerRequest);

    @PostMapping("/login")
    ResponseEntity<ResponseBody<Object>> login(@Valid @RequestBody LoginDTO loginRequest);

    @PostMapping("/forgot-password")
    ResponseEntity<ResponseBody<Object>> forgotPassword(@NotNull @RequestParam(name = "username") String username,
                                                        @NotNull @RequestParam(name = "new-password") String newPssword);

    @PostMapping("/logout")
    ResponseEntity<ResponseBody<Object>> logout(@NotNull @RequestParam(name = "username") String username);

    @GetMapping("/get/user")
    ResponseEntity<ResponseBody<Object>> checkUserExisted(@NotNull @RequestParam(name = "user-input") String userInput);
}