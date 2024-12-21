package com.example.travelingapp.service;

import com.example.travelingapp.response_template.CompleteResponse;

public interface TokenService {
    CompleteResponse<Object> generateJwtToken(String phoneNumber);

    CompleteResponse<Object> refreshJwtToken(String authorizationHeader);

    void storeSessionToken (String userName, String token);

    CompleteResponse<Object> getSessionToken (String userName);

    void invalidateSessionToken (String userName);

    boolean isSessionTokenValid (String userName, String token);

}
