package com.example.travelingapp.controller.impl;

import com.example.travelingapp.controller.UserController;
import com.example.travelingapp.enums.HttpStatusCodeEnum;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.ResponseBody;
import org.springframework.http.ResponseEntity;
import com.example.travelingapp.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<ResponseBody<String>> createNewUserByPhoneNumber(UserDTO registerRequest) {
        return ResponseEntity.status(HttpStatusCodeEnum.OK.value).body(userService.createNewUserByPhoneNumber(registerRequest));
    }

    public ResponseEntity<ResponseBody<String>> createNewUserByEmail(UserDTO registerRequest) {
        return ResponseEntity.status(HttpStatusCodeEnum.OK.value).body(userService.createNewUserByEmail());
    }

    public ResponseEntity<ResponseBody<String>> login(@RequestBody UserDTO loginRequest) {
        return ResponseEntity.status(HttpStatusCodeEnum.OK.value).body(userService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }
}


