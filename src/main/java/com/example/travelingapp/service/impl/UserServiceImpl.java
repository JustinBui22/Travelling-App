package com.example.travelingapp.service.impl;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.entity.Sms;
import com.example.travelingapp.entity.User;
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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.security.data_security.DataAesAlgorithm.encryptData;
import static com.example.travelingapp.util.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.common.DateTimeFormatter.toLocalDate;
import static com.example.travelingapp.Validator.InputValidator.*;
import static com.example.travelingapp.util.common.ErrorCodeResolver.resolveErrorCode;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfigurationRepository configurationRepository;
    private final SmsRepository smsRepository;
    private final SmsServiceImpl smsServiceImpl;
    private final ErrorCodeRepository errorCodeRepository;
    private final TokenServiceImpl tokenServiceImpl;


    public UserServiceImpl(UserRepository userRepository, ConfigurationRepository configurationRepository, SmsRepository smsRepository, SmsServiceImpl smsServiceImpl, ErrorCodeRepository errorCodeRepository, TokenServiceImpl tokenServiceImpl) {
        this.userRepository = userRepository;
        this.configurationRepository = configurationRepository;
        this.smsRepository = smsRepository;
        this.smsServiceImpl = smsServiceImpl;
        this.errorCodeRepository = errorCodeRepository;
        this.tokenServiceImpl = tokenServiceImpl;
    }

    @Override
    public CompleteResponse<Object> createNewUserByPhoneNumber(UserDTO registerRequest) {
        String errorCode;
        Optional<User> user = userRepository.findByUsername(registerRequest.getUsername());

        try {
            if (!validateUsername(registerRequest.getUsername(), configurationRepository.findByConfigCode(USERNAME_PATTERN.name()))) {
                log.info("Username format is invalid!");
                errorCode = resolveErrorCode(errorCodeRepository, USERNAME_FORMAT_INVALID);
            }
            // Check if username is taken
            else if (user.isPresent()) {
                log.info("Username {} is already taken!", user.get().getUsername());
                errorCode = resolveErrorCode(errorCodeRepository, USERNAME_TAKEN);
            }
            // Check if email is inputted and has valid form and if taken
            else if (!registerRequest.getEmail().isEmpty()
                    && !validateEmailForm(registerRequest.getEmail(), configurationRepository.findByConfigCode(EMAIL_PATTERN.name()))) {
                log.info("Email format is invalid");
                errorCode = resolveErrorCode(errorCodeRepository, EMAIL_PATTERN_INVALID);
            } else if (!registerRequest.getEmail().isEmpty() && userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                log.info("Email is already taken!");
                errorCode = resolveErrorCode(errorCodeRepository, EMAIL_TAKEN);
            }
            // Check if the password meets the security requirement
            else if (!validatePassword(registerRequest.getPassword(), configurationRepository.findByConfigCode(PASSWORD_PATTERN.name()))) {
                log.info("Password created is weak!");
                errorCode = resolveErrorCode(errorCodeRepository, PASSWORD_NOT_QUALIFIED);
            }
            // Check if the phone number has a correct format
            else if (!validatePhoneForm(registerRequest.getPhoneNumber(), configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                log.info("Phone format is invalid");
                errorCode = resolveErrorCode(errorCodeRepository, PHONE_FORMAT_INVALID);
            } else {
                // Get sms config for sms otp verification
                Optional<Sms> registerSmsOptional = smsRepository.findBySmsCodeAndSmsFlow(SmsEnum.SMS_OTP_REGISTER.getCode(), Register.name());
                if (registerSmsOptional.isPresent()) {
                    String registerMessage = registerSmsOptional.get().getSmsContent();
                    log.info("Start sending sms {} for otp verification in {} flow !", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                    smsServiceImpl.sendSms(registerRequest.getPhoneNumber(), registerMessage);

                    User newUser = new User(registerRequest.getUsername(), encryptData(registerRequest.getPassword()),
                            registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now(), registerRequest.getEmail(), true);
                    userRepository.save(newUser);
                    log.info("User has been created!");
                    errorCode = resolveErrorCode(errorCodeRepository, USER_CREATED);
                } else {
                    log.info("There is no config for sms {} for {} flow!", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                    errorCode = resolveErrorCode(errorCodeRepository, SMS_NOT_CONFIG);
                }
            }
            return getCompleteResponse(errorCodeRepository, errorCode, Register.name());
        } catch (Exception e) {
            log.error("There has been an error in registering a new user!", e);
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
        // Validate if the username is a phone number
        boolean isPhoneNumber = validatePhoneForm(
                username,
                configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name())
        );
        // Retrieve the user based on username type (phone number or normal username)
        Optional<User> userOptional = isPhoneNumber ?
                userRepository.findByPhoneNumberAndStatus(username, true) :
                userRepository.findByUsername(username);
        try {
            // Handle user not found
            if (userOptional.isEmpty()) {
                log.info("User {} not found!", username);
                errorCode = resolveErrorCode(errorCodeRepository, USER_NOT_FOUND);
            } else {
                User user = userOptional.get();
                // check if password matches and display corresponding error code.
                boolean isPasswordCorrect = encryptData(password).equals(user.getPassword());
                errorCode = isPasswordCorrect
                        ? resolveErrorCode(errorCodeRepository, LOGIN_SUCCESS)
                        : resolveErrorCode(errorCodeRepository, PASSWORD_NOT_CORRECT);
                log.info(encryptData(password).equals(user.getPassword())
                        ? "User {} logged in successfully!"
                        : "Password incorrect!", username);

                // Establishes the authentication context for the session after successful login.
                if (isPasswordCorrect) {
                    log.info("Current user: {}", user.getPhoneNumber());
                    // Create an authentication object from the user
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user, user.getPhoneNumber(), user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Change userName to phoneNumber
                    if (!isPhoneNumber) {
                        username = String.valueOf(authentication.getCredentials());
                    }
                    // Generate and return the JWT token
                    String token = tokenServiceImpl.generateToken(username).getResponseBody().getBody().toString();
                    return getCompleteResponse(errorCodeRepository, errorCode, LOGIN_SUCCESS.name(), Login.name(), token);
                }
            }
            return getCompleteResponse(errorCodeRepository, errorCode, Login.name());
        } catch (Exception e) {
            log.error("There has been an error in logging in for user {}!", username, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompleteResponse<Object> test(String input) {
        return new CompleteResponse<>(new ResponseBody<>(null, null, null, encryptData(input)), HttpStatusCodeEnum.OK.value());

    }
}
