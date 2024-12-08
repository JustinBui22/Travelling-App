package com.example.travelingapp.controller.impl;

import com.example.travelingapp.controller.UserController;
import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.CompleteResponse;
import com.example.travelingapp.util.ResponseBody;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import com.example.travelingapp.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<ResponseBody<Object>> createNewUserByPhoneNumber(UserDTO registerRequest) {
        CompleteResponse<Object> response = userService.createNewUserByPhoneNumber(registerRequest);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));

    }

    public ResponseEntity<ResponseBody<Object>> createNewUserByEmail(UserDTO registerRequest) {
        CompleteResponse<Object> response = userService.createNewUserByEmail(registerRequest);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));
    }

    public ResponseEntity<ResponseBody<Object>> login(LoginDTO loginRequest) {
        CompleteResponse<Object> response = userService.login(loginRequest);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));
    }
}


