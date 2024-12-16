package com.example.travelingapp.controller.impl;

import com.example.travelingapp.controller.TokenController;
import com.example.travelingapp.service.TokenService;
import com.example.travelingapp.response_template.CompleteResponse;
import com.example.travelingapp.response_template.ResponseBody;
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

    @Override
    public ResponseEntity<ResponseBody<Object>> refreshToken(String authorizationHeader) {
        CompleteResponse<Object> response = tokenService.refreshToken(authorizationHeader);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));
    }
}
