package com.example.travelingapp.service.impl;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.entity.Sms;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.enums.SmsEnum;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.SmsRepository;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.response_template.CompleteResponse;
import lombok.extern.log4j.Log4j2;
import com.example.travelingapp.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.response_template.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.DateTimeFormatter.toLocalDate;
import static com.example.travelingapp.Validator.InputValidator.*;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfigurationRepository configurationRepository;
    private final SmsRepository smsRepository;
    private final SmsServiceImpl smsServiceImpl;
    private final ErrorCodeRepository errorCodeRepository;
    private final TokenServiceImpl tokenServiceImpl;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, ConfigurationRepository configurationRepository, SmsRepository smsRepository, SmsServiceImpl smsServiceImpl, ErrorCodeRepository errorCodeRepository, TokenServiceImpl tokenServiceImpl, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.configurationRepository = configurationRepository;
        this.smsRepository = smsRepository;
        this.smsServiceImpl = smsServiceImpl;
        this.errorCodeRepository = errorCodeRepository;
        this.tokenServiceImpl = tokenServiceImpl;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CompleteResponse<Object> createNewUser(UserDTO registerRequest) {
        ErrorCodeEnum errorCodeEnum;
        Optional<User> user = userRepository.findByUsername(registerRequest.getUsername());

        try {
            if (!validateUsername(registerRequest.getUsername(), configurationRepository.findByConfigCode(USERNAME_PATTERN.name()))) {
                log.info("Username format is invalid!");
                errorCodeEnum = USERNAME_FORMAT_INVALID;
            }
            // Check if username is taken
            else if (user.isPresent()) {
                log.info("Username {} is already taken!", user.get().getUsername());
                errorCodeEnum = USERNAME_TAKEN;
            }
            // Check if email is inputted and has valid form and if taken
            else if (!registerRequest.getEmail().isEmpty() && !validateEmailForm(registerRequest.getEmail(), configurationRepository.findByConfigCode(EMAIL_PATTERN.name()))) {
                log.info("Email format is invalid");
                errorCodeEnum = EMAIL_PATTERN_INVALID;
            } else if (!registerRequest.getEmail().isEmpty() && userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                log.info("Email is already taken!");
                errorCodeEnum = EMAIL_TAKEN;
            }
            // Check if the password meets the security requirement
            else if (!validatePassword(registerRequest.getPassword(), configurationRepository.findByConfigCode(PASSWORD_PATTERN.name()))) {
                log.info("Password created is weak!");
                errorCodeEnum = PASSWORD_NOT_QUALIFIED;
            }
            // Check if the phone number has a correct format
            else if (!registerRequest.getPhoneNumber().isEmpty() && !validatePhoneForm(registerRequest.getPhoneNumber(), configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                log.info("Phone format is invalid");
                errorCodeEnum = PHONE_FORMAT_INVALID;
            } else {
                if (!registerRequest.getPhoneNumber().isEmpty()) {
                    // Get sms config for sms otp verification
                    Optional<Sms> registerSmsOptional = smsRepository.findBySmsCodeAndSmsFlow(SmsEnum.SMS_OTP_REGISTER.getCode(), Register.name());
                    if (registerSmsOptional.isPresent()) {
                        String registerMessage = registerSmsOptional.get().getSmsContent();
                        log.info("Start sending sms {} for otp verification in {} flow !", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                        smsServiceImpl.sendSms(registerRequest.getPhoneNumber(), registerMessage);
                    } else {
                        log.error("There is no config for sms {} for {} flow!", SmsEnum.SMS_OTP_REGISTER.name(), SmsEnum.SMS_OTP_REGISTER.getFlow());
                        throw new BusinessException(SMS_NOT_CONFIG, Register.name());
                    }
                } else if (!registerRequest.getEmail().isEmpty()) {
                    // Send verification code through email
                } else {
                    // Need either phone number or email for sign-up verification
                    log.warn("Need either phone number or email for sign-up OTP verification!");
                    throw new BusinessException(INTERNAL_SERVER_ERROR, Register.name());
                }
                // Check if OTP code is verified
                if (validateRegistrationOtpCode()) {
                    User newUser = new User(registerRequest.getUsername(), passwordEncoder.encode(registerRequest.getPassword()), registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now(), registerRequest.getEmail(), true, false);
                    userRepository.save(newUser);
                    log.info("User has been created!");
                    errorCodeEnum = USER_CREATED;
                } else {
                    log.error("OTP verification failed!");
                    throw new BusinessException(OTP_VERIFICATION_FAIL, Register.name());
                }
            }
            return getCompleteResponse(errorCodeRepository, errorCodeEnum, Register.name(), null);
        } catch (
                Exception e) {
            log.error("There has been an error in registering a new user!", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, Register.name());
        }
    }

    private boolean validateRegistrationOtpCode() {
        return true;
    }

    @Override
    public CompleteResponse<Object> createNewUserByEmail(UserDTO registerRequest) {
        return null;
    }

    @Override
    public CompleteResponse<Object> login(LoginDTO loginRequest) {
        String username = loginRequest.getUsername();
        ErrorCodeEnum errorCodeEnum;
        boolean isPhoneNumber = validatePhoneForm(username, configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()));
        // Retrieve the user based on username type (phone number or username)
        Optional<User> userOptional = isPhoneNumber ? userRepository.findByPhoneNumberAndStatus(username, true) : userRepository.findByUsernameAndStatus(username, true);
        try {
            if (userOptional.isEmpty()) {
                log.error("User {} not found!", username);
                throw new BusinessException(USER_NOT_FOUND, Register.name());
            }
            User user = userOptional.get();
            // check if password matches and display corresponding error code.
            boolean isPasswordCorrect = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            errorCodeEnum = isPasswordCorrect ? LOGIN_SUCCESS : PASSWORD_NOT_CORRECT;
            log.info(isPasswordCorrect ? "User {} logged in successfully!" : "Password incorrect!", username);
            // Establishes the authentication context for the session after successful login.
            if (isPasswordCorrect) {
                log.info("Current user: {}", user.getUsername());
                // Create an authentication object from the user
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Change phoneNumber to userName
                if (isPhoneNumber) {
                    username = String.valueOf(authentication.getCredentials());
                }
                // Generate and return the JWT token
                String token = tokenServiceImpl.generateJwtToken(username).getResponseBody().getBody().toString();
                return getCompleteResponse(errorCodeRepository, errorCodeEnum, Login.name(), token);
            }
            return getCompleteResponse(errorCodeRepository, errorCodeEnum, Login.name(), null);
        } catch (Exception e) {
            log.error("There has been an error in logging in for user {}!", username, e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
    }

    @Override
    public CompleteResponse<Object> test(String input) {
        if (input.isEmpty()) {
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
        return getCompleteResponse(errorCodeRepository, LOGIN_SUCCESS, Test.name(), passwordEncoder.encode(input));
    }
}