package com.example.travelingapp.service.impl;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.entity.Sms;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.enums.HttpStatusCodeEnum;
import com.example.travelingapp.enums.SmsEnum;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.SmsRepository;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.CompleteResponse;
import com.example.travelingapp.util.ResponseBody;
import lombok.extern.log4j.Log4j2;
import com.example.travelingapp.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.security.data_security.DataAesAlgorithm.encryptData;
import static com.example.travelingapp.util.DateTimeFormatter.toLocalDate;
import static com.example.travelingapp.Validator.Validator.*;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ErrorCodeRepository errorCodeRepository;
    private final ConfigurationRepository configurationRepository;
    private final SmsRepository smsRepository;
    private final SmsServiceImpl smsService;

    public UserServiceImpl(UserRepository userRepository, ErrorCodeRepository errorCodeRepository, ConfigurationRepository configurationRepository, SmsRepository smsRepository, SmsServiceImpl smsService) {
        this.userRepository = userRepository;
        this.errorCodeRepository = errorCodeRepository;
        this.configurationRepository = configurationRepository;
        this.smsRepository = smsRepository;



        this.smsService = smsService;
    }

    @Override
    public CompleteResponse<Object> createNewUserByPhoneNumber(UserDTO registerRequest) {
        String errorCode;
        HttpStatusCodeEnum httpStatusCode;
        String errorMessage;
        String errorDescription;
        Optional<User> user = userRepository.findByUsername(registerRequest.getUsername());

        try {
            if (!validateUsername(registerRequest.getUsername(), configurationRepository.findByConfigCode(USERNAME_PATTERN.name()))) {
                log.info("Username format is invalid!");
                errorCode = resolveErrorCode(USERNAME_FORMAT_INVALID);
            }
            // Check if username is taken
            else if (user.isPresent()) {
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

                    User newUser = new User(registerRequest.getUsername(), encryptData(registerRequest.getPassword()),
                            registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now());
                    userRepository.save(newUser);
                    log.info("User has been created!");
                    errorCode = resolveErrorCode(USER_CREATED);
                } else {
                    log.info("There is no config for sms {} for {} flow!", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                    errorCode = resolveErrorCode(SMS_NOT_CONFIG);
                }
            }
            httpStatusCode = getHttpFromErrorCode(errorCode);
            errorMessage = errorCodeRepository.findByErrorCode(errorCode).isPresent() ? errorCodeRepository.findByErrorCode(errorCode).get().getErrorMessage() : UNDEFINED_ERROR_CODE.getMessage();
            errorDescription = errorCodeRepository.findByErrorCode(errorCode).isPresent() ? errorCodeRepository.findByErrorCode(errorCode).get().getErrorDescription() : null;
            return new CompleteResponse<>(new ResponseBody<>(errorCode, errorMessage, Register.name(), errorDescription), httpStatusCode.value());
        } catch (Exception e) {
            log.info("There has been an error in registering a new user!", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompleteResponse<Object> createNewUserByEmail(UserDTO registerRequest) {
        return null;
    }

    @Override
    public CompleteResponse<Object> login(LoginDTO loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        String errorCode;
        HttpStatusCodeEnum httpStatusCode;
        String errorMessage = UNDEFINED_ERROR_CODE.getMessage();
        String errorDescription = null;

        try {
            // Validate if the username is a phone number
            boolean isPhoneNumber = validatePhoneForm(
                    username,
                    configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name())
            );

            // Retrieve the user based on username type (phone number or normal username)
            Optional<User> user = isPhoneNumber ?
                    userRepository.findByPhoneNumber(username) :
                    userRepository.findByUsername(username);

            // Handle user not found
            if (user.isEmpty()) {
                log.info("Username not found!");
                errorCode = resolveErrorCode(USER_NOT_FOUND);
            } else {
                User foundUser = user.get();

                // Verify the password
                if (!password.equals(foundUser.getPassword())) {
                    log.info("Password incorrect!");
                    errorCode = resolveErrorCode(PASSWORD_NOT_CORRECT);
                } else {
                    log.info("User logged in successfully!");
                    errorCode = resolveErrorCode(LOGIN_SUCCESS);
                }
            }

            // Resolve HTTP status and error details
            httpStatusCode = getHttpFromErrorCode(errorCode);
            Optional<ErrorCode> resolvedErrorCode = errorCodeRepository.findByErrorCode(errorCode);

            if (resolvedErrorCode.isPresent()) {
                ErrorCode error = resolvedErrorCode.get();
                errorMessage = error.getErrorMessage();
                errorDescription = error.getErrorDescription();
            }
            // Prepare and return the response
            return new CompleteResponse<>(
                    new ResponseBody<>(errorCode, errorMessage, Login.name(), errorDescription),
                    httpStatusCode.value()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompleteResponse<Object> test(String input) {
        return new CompleteResponse<>(new ResponseBody<>(null, null, null, encryptData(input)), HttpStatusCodeEnum.OK.value());

    }

    private String resolveErrorCode(ErrorCodeEnum errorCodeEnum) {
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByHttpCode(String.valueOf(errorCodeEnum));
        return errorCodeOptional.map(ErrorCode::getErrorCode)
                .orElse(errorCodeEnum.getCode().isEmpty() ? UNDEFINED_ERROR_CODE.getCode() : errorCodeEnum.getCode());
    }
}
