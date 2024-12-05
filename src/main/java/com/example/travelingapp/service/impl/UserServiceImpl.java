package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.DataEncrypt;
import com.example.travelingapp.util.ResponseBody;
import lombok.extern.log4j.Log4j2;
import com.example.travelingapp.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.travelingapp.enums.Enum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.util.DateTimeFormatter.toLocalDate;
import static com.example.travelingapp.util.Validator.*;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ErrorCodeRepository errorCodeRepository;
    private final ConfigurationRepository configurationRepository;
    private final DataEncrypt dataEncrypt = new DataEncrypt();


    public UserServiceImpl(UserRepository userRepository, ErrorCodeRepository errorCodeRepository, ConfigurationRepository configurationRepository) {
        this.userRepository = userRepository;
        this.errorCodeRepository = errorCodeRepository;
        this.configurationRepository = configurationRepository;

    }

    @Override
    public ResponseBody<String> createNewUserByPhoneNumber(UserDTO registerRequest) {
        String errorCode;
        String message;
        String httpStatusCode;
        Optional<User> user = userRepository.findByUsername(registerRequest.getUsername());

        try {
            // Check if username is taken
            if (user.isPresent()) {
                log.info("Username {} is already taken!", user.get().getUsername());
                errorCode = resolveErrorCode(USERNAME_TAKEN);
            }
            // Check if email is inputted and has valid form and if taken
            else if (!registerRequest.getEmail().isEmpty()
                    && !validateEmailForm(registerRequest.getEmail(), configurationRepository.findByConfigCode(EMAIL_PATTERN.name()))) {
                log.info("Email format is invalid");
                errorCode = resolveErrorCode(EMAIL_PATTERN_INVALID);
            } else if (!registerRequest.getEmail().isEmpty() && userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                log.info("Email is already taken!");
                errorCode = resolveErrorCode(EMAIL_TAKEN);
            }
            // Check if the password meets the security requirement
            else if (!validatePassword(registerRequest.getPassword(), configurationRepository.findByConfigCode(PASSWORD_PATTERN.name()))) {
                log.info("Password created is weak!");
                errorCode = resolveErrorCode(PASSWORD_NOT_QUALIFIED);
            }
            // Check if the phone number has a correct format
            else if (!validatePhoneForm(registerRequest.getPhoneNumber(), configurationRepository.findByConfigCode(PASSWORD_PATTERN.name()))) {
                log.info("Phone format is invalid");
                errorCode = resolveErrorCode(PHONE_FORMAT_INVALID);
            } else {
                User newUser = new User(registerRequest.getUsername(), dataEncrypt.encryptData(registerRequest.getPassword()),
                        registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now());
                userRepository.save(newUser);
                log.info("User has been created!");
                errorCode = resolveErrorCode(USER_CREATED);
            }
            httpStatusCode = String.valueOf(getHttpFromErrorCode(errorCode));
            message = errorCodeRepository.findByErrorCode(errorCode).isPresent() ? errorCodeRepository.findByErrorCode(errorCode).get().getErrorMessage() : UNDEFINED_ERROR_CODE.getMessage();
            return new ResponseBody<>(errorCode, message, Register.name(), !httpStatusCode.isEmpty() ? httpStatusCode : String.valueOf(UNDEFINED_HTTP_CODE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseBody<String> createNewUserByEmail() {
        return null;
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

    private String resolveErrorCode(ErrorCodeEnum errorCodeEnum) {
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByHttpCode(String.valueOf(errorCodeEnum));
        return errorCodeOptional.map(ErrorCode::getErrorCode)
                .orElse(errorCodeEnum.getCode().isEmpty() ? UNDEFINED_ERROR_CODE.getCode() : errorCodeEnum.getCode());
    }
}
