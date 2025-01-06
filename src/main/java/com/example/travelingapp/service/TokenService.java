package com.example.travelingapp.service;

import com.example.travelingapp.response_template.CompleteResponse;

public interface TokenService {
    CompleteResponse<Object> generateJwtToken(String userName);

    CompleteResponse<Object> refreshJwtToken(String authorizationHeader, String sessionToken, String userName);

    CompleteResponse<Object> getActiveSessionToken(String userName);

    CompleteResponse<Object> generateSessionToken(String userName);

    void invalidateOldestSessionToken(String userName);

    boolean isSessionTokenValid(String userName, String token);
}
