package com.example.travelingapp.service;

import com.example.travelingapp.response_template.CompleteResponse;

public interface TokenService {
    CompleteResponse<Object> generateJwtToken(String userName);

    CompleteResponse<Object> refreshJwtToken(String authorizationHeader, String sessionToken, String userName);

    CompleteResponse<Object> generateSessionToken(String userName);

    void revokeSessionTokens(String userName);

    boolean isSessionTokenInvalid(String userName, String token);
}
