package com.example.travelingapp.controller.impl;

import com.example.travelingapp.controller.UserController;
import com.example.travelingapp.util.HttpStatusCode;
import com.example.travelingapp.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.travelingapp.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<HttpStatusCode> createNewUser(@RequestBody UserDTO registerRequest) {
        int response = userService.createNewUser(registerRequest);
        return ResponseEntity.status(response).body(HttpStatusCode.valueOf(response));
    }

    public ResponseEntity<HttpStatusCode> login(@RequestBody UserDTO loginRequest) {
        int response = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.status(response).body(HttpStatusCode.valueOf(response));
    }
}


