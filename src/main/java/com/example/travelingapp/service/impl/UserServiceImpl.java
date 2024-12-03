package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.User;
import com.example.travelingapp.repository.ErrorRepository;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.ResponseBody;
import lombok.extern.log4j.Log4j2;
import com.example.travelingapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.travelingapp.enums.ErrorCode.*;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ErrorRepository errorRepository;

    public UserServiceImpl(UserRepository userRepository, ErrorRepository errorRepository) {
        this.userRepository = userRepository;
        this.errorRepository = errorRepository;
    }

    @Override
    public ResponseBody<String> createNewUserByUsername(UserDTO registerRequest) {
        String errorCode;
        String message;
        String httpStatusCode = "";
        Optional<User> user = userRepository.findByUsername(registerRequest.getUsername());

        try {
            if (user.isPresent()) {
                log.info("Username is already taken!");
                errorCode = USERNAME_TAKEN.getCode();
                httpStatusCode = USERNAME_TAKEN.getHttpStatusCode().toString();
            } else if (!registerRequest.getEmail().isEmpty() && userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                log.info("Email is already taken!");
                errorCode = EMAIL_TAKEN.getCode();
            } else if (registerRequest.getPassword().isEmpty() || !isQualifiedPassword(registerRequest.getPassword())) {
                log.info("Password created is weak!");
                errorCode = PASSWORD_NOT_QUALIFIED.getCode();
            } else {
                User newUser = new User(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
                userRepository.save(newUser);
                log.info("User has been created!");
                errorCode = USER_CREATED.getCode();
            }
            message = errorRepository.findByErrorCode(errorCode).isPresent() ? errorRepository.findByErrorCode(errorCode).get().getErrorMessage() : UNDEFINED_ERROR_CODE.getMessage();
            return new ResponseBody<>(errorCode, httpStatusCode, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseBody<String> login(String username, String password) {
        String responseCode;
        String message;
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.info("User not found");
            responseCode = USER_NOT_FOUND.getCode();
        }
//        if (user.isPresent() && !user.get().getPassword().equals(password)) {
//            log.info("Password is not correct");
//            responseCode = HttpStatusCode.UNAUTHORIZED.value();
//        } else {
//            log.info("Login successfully!");
//            responseCode = HttpStatusCode.ACCEPTED.value();
//        }
//        message = HttpStatusCode.valueOf(responseCode).getReasonPhrase();
//        return new ResponseBody<>(responseCode, message, "Login");
        return null;
    }

    private boolean isQualifiedPassword(String password) {
        return true;
    }
}
