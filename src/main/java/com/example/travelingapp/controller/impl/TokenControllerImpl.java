package com.example.travelingapp.controller.impl;

import com.example.travelingapp.controller.TokenController;
import com.example.travelingapp.service.TokenService;
import com.example.travelingapp.util.CompleteResponse;
import com.example.travelingapp.util.ResponseBody;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenControllerImpl implements TokenController {
    private final TokenService tokenService;

    public TokenControllerImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<ResponseBody<Object>> generateToken(String phoneNumber) {
        CompleteResponse<Object> response = tokenService.generateToken(phoneNumber);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));

    }
}
