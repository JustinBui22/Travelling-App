package com.example.travelingapp.service;

import com.example.travelingapp.response_template.CompleteResponse;

public interface TokenService {
    CompleteResponse<Object> generateToken(String phoneNumber);

    CompleteResponse<Object> refreshToken (String authorizationHeader);

}
