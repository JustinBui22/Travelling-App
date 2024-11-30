package com.example.travelingapp.service;

import com.example.travelingapp.dto.UserDTO;

public interface UserService {
    int createNewUser(UserDTO registerRequest);
    int login(String username, String password);
}
