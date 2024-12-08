package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.entity.Sms;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.enums.SmsEnum;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.SmsRepository;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.security.DataSecurity;
import com.example.travelingapp.util.ResponseBody;
import lombok.extern.log4j.Log4j2;
import com.example.travelingapp.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.util.DateTimeFormatter.toLocalDate;
import static com.example.travelingapp.Validator.Validator.*;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ErrorCodeRepository errorCodeRepository;
    private final ConfigurationRepository configurationRepository;
    private final SmsRepository smsRepository;
    private final DataSecurity dataSecurity = new DataSecurity();
    private final SmsServiceImpl smsService;
    private final View error;


    public UserServiceImpl(UserRepository userRepository, ErrorCodeRepository errorCodeRepository, ConfigurationRepository configurationRepository, SmsRepository smsRepository, SmsServiceImpl smsService, View error) {
        this.userRepository = userRepository;
        this.errorCodeRepository = errorCodeRepository;
        this.configurationRepository = configurationRepository;
        this.smsRepository = smsRepository;

        this.smsService = smsService;
        this.error = error;
    }

    @Override
    public ResponseBody<String> createNewUserByPhoneNumber(UserDTO registerRequest) {
        String errorCode;
        String httpStatusCode;
        String message;
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
            else if (!validatePhoneForm(registerRequest.getPhoneNumber(), configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                log.info("Phone format is invalid");
                errorCode = resolveErrorCode(PHONE_FORMAT_INVALID);
            } else {
                // Get sms config for sms otp verification
                Optional<Sms> registerSmsOptional = smsRepository.findBySmsCodeAndSmsFlow(SmsEnum.SMS_OTP_REGISTER.getCode(), Register.name());
                if (registerSmsOptional.isPresent()) {
                    String registerMessage = registerSmsOptional.get().getSmsContent();
                    log.info("Start sending sms {} for otp verification in {} flow !", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                    smsService.sendSms(registerRequest.getPhoneNumber(), registerMessage);

                    User newUser = new User(registerRequest.getUsername(), dataSecurity.encryptData(registerRequest.getPassword()),
                            registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now());
                    userRepository.save(newUser);
                    log.info("User has been created!");
                    errorCode = resolveErrorCode(USER_CREATED);
                } else {
                    log.info("There is no config for sms {} for {} flow!", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                    errorCode = resolveErrorCode(SMS_NOT_CONFIG);
                }
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
        String errorCode;
        String httpStatusCode;
        String message;

        try {
            // Validate if the username is a phone number
            boolean isPhoneNumber = validatePhoneForm(username, configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()));

            // Retrieve the user based on username type (phone number or normal username)
            Optional<User> user = isPhoneNumber ? userRepository.findByPhoneNumber(username) : userRepository.findByUsername(username);

            // If user is not found
            if (user.isEmpty()) {
                log.info("Invalid username or password!");
                errorCode = resolveErrorCode(USERNAME_PASSWORD_NOT_CORRECT);
            } else {
                User foundUser = user.get();

                // Verify the password
                if (!password.equals(foundUser.getPassword())) {
                    log.info("Invalid username or password!");
                    errorCode = resolveErrorCode(USERNAME_PASSWORD_NOT_CORRECT);
                } else {
                    log.info("User logged in successfully!");
                    errorCode = resolveErrorCode(LOGIN_SUCCESS);
                }
            }

            // Prepare the response
            httpStatusCode = String.valueOf(getHttpFromErrorCode(errorCode));
            message = errorCodeRepository.findByErrorCode(errorCode).isPresent() ? errorCodeRepository.findByErrorCode(errorCode).get().getErrorMessage() : UNDEFINED_ERROR_CODE.getMessage();

            return new ResponseBody<>(errorCode, message, Login.name(), !httpStatusCode.isEmpty() ? httpStatusCode : String.valueOf(UNDEFINED_HTTP_CODE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String resolveErrorCode(ErrorCodeEnum errorCodeEnum) {
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByHttpCode(String.valueOf(errorCodeEnum));
        return errorCodeOptional.map(ErrorCode::getErrorCode)
                .orElse(errorCodeEnum.getCode().isEmpty() ? UNDEFINED_ERROR_CODE.getCode() : errorCodeEnum.getCode());
    }
}
