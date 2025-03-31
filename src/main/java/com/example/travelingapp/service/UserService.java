package com.example.travelingapp.service;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.response_template.CompleteResponse;

public interface UserService {
    CompleteResponse<Object> createNewUser(UserDTO registerRequest);

    CompleteResponse<Object> resetPassword(String username, String newPassword);

    CompleteResponse<Object> login(LoginDTO loginRequest);

    CompleteResponse<Object> logout(String username);

    CompleteResponse<Object> checkUserExisted(String userInput);
}
