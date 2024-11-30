package service;

import dto.UserDTO;

public interface UserService {
    int createNewUser (UserDTO registerRequest);
    int login(String username, String password);
}
