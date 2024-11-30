package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.User;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.util.HttpStatusCode;
import com.example.travelingapp.dto.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.example.travelingapp.repository.UserRepository;

import java.util.Optional;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public int createNewUser(UserDTO registerRequest) {
        Optional<User> user = userRepository.findByUsername(registerRequest.getUsername());
        if (user.isPresent()) {
            log.info("Username is already taken!");
            return HttpStatusCode.USERNAME_TAKEN.value();
        }
        if (!registerRequest.getEmail().isEmpty() && userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.info("Email is already registered!");
            return HttpStatusCode.EMAIL_TAKEN.value();
        }
        if (!isQualifiedPassword(registerRequest.getPassword())) {
            return HttpStatusCode.PASSWORD_NOT_QUALIFIED.value();
        }
        User newUser = new User(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
        userRepository.save(newUser);
        log.info("User has been created!");

        return HttpStatusCode.USER_CREATED.value();
    }

    @Override
    public int login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.info("User not found");
            return HttpStatusCode.NOT_FOUND.value();
        }
        if (!user.get().getPassword().equals(password)) {
            log.info("Password is not correct");
            return HttpStatusCode.UNAUTHORIZED.value();
        }
        log.info("Login successfully!");
        return HttpStatusCode.ACCEPTED.value();
    }

    private boolean isQualifiedPassword(String password) {
        return true;
    }
}
