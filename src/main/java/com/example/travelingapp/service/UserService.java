package com.example.travelingapp.service;

import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.CompleteResponse;

public interface UserService {
    CompleteResponse<Object> createNewUserByPhoneNumber(UserDTO registerRequest);
    CompleteResponse<Object> createNewUserByEmail(UserDTO registerRequest);

    CompleteResponse<Object> login(UserDTO registerRequest);
}
