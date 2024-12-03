package com.example.travelingapp.service;

import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.ResponseBody;

public interface UserService {
    ResponseBody<String> createNewUserByUsername(UserDTO registerRequest);
    ResponseBody<String> login(String username, String password);
}
