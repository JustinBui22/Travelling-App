package com.example.travelingapp.service.impl;

import com.example.travelingapp.dto.LoginDTO;
import com.example.travelingapp.dto.OtpDTO;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.service.UserService;
import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.response_template.CompleteResponse;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import com.example.travelingapp.repository.UserRepository;

import java.time.LocalDate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.response_template.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.Common.findUser;
import static com.example.travelingapp.util.DateTimeFormatter.toLocalDate;
import static com.example.travelingapp.validator.InputValidator.*;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfigurationRepository configurationRepository;
    private final ErrorCodeRepository errorCodeRepository;
    private final TokenServiceImpl tokenServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final OtpServiceImpl otpServiceImpl;


    public UserServiceImpl(UserRepository userRepository, ConfigurationRepository configurationRepository, ErrorCodeRepository errorCodeRepository, TokenServiceImpl tokenServiceImpl, PasswordEncoder passwordEncoder, OtpServiceImpl otpServiceImpl) {
        this.userRepository = userRepository;
        this.configurationRepository = configurationRepository;
        this.errorCodeRepository = errorCodeRepository;
        this.tokenServiceImpl = tokenServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.otpServiceImpl = otpServiceImpl;
    }

    @Override
    public CompleteResponse<Object> createNewUser(UserDTO registerRequest) {
        ErrorCodeEnum errorCodeEnum;
        Optional<User> userOptional = userRepository.findByUsernameAndActive(registerRequest.getUsername(), true);
        try {
            if (!validateUsername(registerRequest.getUsername(), configurationRepository.findByConfigCode(USERNAME_PATTERN.name()))) {
                log.info("Username format is invalid!");
                throw new BusinessException(USERNAME_FORMAT_INVALID, REGISTER.name());
            }
            // Check if username is taken
            else if (userOptional.isPresent()) {
                log.info("Username {} is already taken!", userOptional.get().getUsername());
                throw new BusinessException(USERNAME_TAKEN, REGISTER.name());
            }
            // Check if email is inputted and has valid form and if taken
            else if (!StringUtils.isEmpty(registerRequest.getEmail()) && !validateEmailForm(registerRequest.getEmail(), configurationRepository.findByConfigCode(EMAIL_PATTERN.name()))) {
                log.info("Email format is invalid");
                throw new BusinessException(EMAIL_PATTERN_INVALID, REGISTER.name());
            } else if (!StringUtils.isEmpty(registerRequest.getEmail()) && userRepository.findByEmailAndActive(registerRequest.getEmail(), true).isPresent()) {
                log.info("Email is already taken!");
                throw new BusinessException(EMAIL_TAKEN, REGISTER.name());
            }
            // Check if the password meets the security requirement
            else if (!validatePassword(registerRequest.getPassword(), configurationRepository.findByConfigCode(PASSWORD_PATTERN.name()))) {
                log.info("Password created is weak!");
                throw new BusinessException(PASSWORD_NOT_QUALIFIED, REGISTER.name());
            }
            // Check if the phone number has a correct format
            else if (!StringUtils.isEmpty(registerRequest.getPhoneNumber()) && !validatePhoneForm(registerRequest.getPhoneNumber(), configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                log.info("Phone format is invalid");
                throw new BusinessException(PHONE_FORMAT_INVALID, REGISTER.name());
            }
            // Check if OTP code is verified
//            String verifyOtpErrorCode = otpServiceImpl.verifyOtp(new OtpDTO(registerRequest.getUsername(), registerRequest.getOtp())).getResponseBody().getCode();
            User newUser = new User(registerRequest.getUsername(), passwordEncoder.encode(registerRequest.getPassword()), registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now(), registerRequest.getEmail(), true);
            userRepository.save(newUser);
            log.info("User has been created!");
            errorCodeEnum = USER_CREATED;

//            if (verifyOtpErrorCode.equals(OTP_VERIFICATION_SUCCESS.getCode())) {
//                User newUser = new User(registerRequest.getUsername(), passwordEncoder.encode(registerRequest.getPassword()), registerRequest.getPhoneNumber(), toLocalDate(registerRequest.getDob()), LocalDate.now(), registerRequest.getEmail(), true);
//                userRepository.save(newUser);
//                log.info("User has been created!");
//                errorCodeEnum = USER_EXISTED;
//            } else {
//                log.error("OTP verification failed!");
//                throw new BusinessException(OTP_VERIFICATION_FAIL, REGISTER.name());
//            }
            return getCompleteResponse(errorCodeRepository, errorCodeEnum, REGISTER.name(), null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("There has been an error in registering a new user!", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, REGISTER.name());
        }
    }

    @Override
    public CompleteResponse<Object> resetPassword(String username, String newPassword) {
        // Check if email/phone existed
        try {
            Optional<User> userOptional = userRepository.findByUsernameAndActive(username, true);
            if (userOptional.isEmpty()) {
                log.error("User {} not found to reset password!", username);
                throw new BusinessException(USER_NOT_FOUND, FORGOT_PASSWORD.name());
            }
            User user = userOptional.get();
            // Update new password to db
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return getCompleteResponse(errorCodeRepository, RESET_PASSWORD_SUCCESS, COMMON.name(), userOptional.get().getUsername());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("There has been an error in resetting password for user {}!", username, e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }

    @Override
    public CompleteResponse<Object> checkUserExisted(String userInput) {
        Optional<User> userOptional = findUser(userInput, configurationRepository, userRepository);
        if (userOptional.isEmpty()) {
            log.error("User {} not found!", userInput);
            throw new BusinessException(USER_NOT_FOUND, FORGOT_PASSWORD.name());
        }
        return getCompleteResponse(errorCodeRepository, USER_EXISTED, COMMON.name(), userOptional.get().getUsername());
    }

    @Override
    public CompleteResponse<Object> login(LoginDTO loginRequest) {
        String username = loginRequest.getUsername();
        // Check if the user has exceeded maxed number of active sessions
        tokenServiceImpl.isExceedMaxAllowedSessions(username);
        ErrorCodeEnum errorCodeEnum;
        try {
            Optional<User> userOptional = findUser(username, configurationRepository, userRepository);
            if (userOptional.isEmpty()) {
                log.error("User {} not found to login!", username);
                throw new BusinessException(USER_NOT_FOUND, LOGIN.name());
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
                Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Change phoneNumber to userName
                if (validatePhoneForm(username, configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                    username = String.valueOf(authentication.getCredentials());
                }
                // Generate and return the session and JWT token
                String jwtToken = tokenServiceImpl.generateJwtToken(username).getResponseBody().getBody().toString();
                String sessionToken = tokenServiceImpl.generateSessionToken(username).getResponseBody().getBody().toString();
                Map<String, String> tokenMap = new HashMap<>();
                tokenMap.put("jwtToken", jwtToken);
                tokenMap.put("sessionToken", sessionToken);
                return getCompleteResponse(errorCodeRepository, errorCodeEnum, LOGIN.name(), tokenMap);
            }
            return getCompleteResponse(errorCodeRepository, errorCodeEnum, LOGIN.name(), null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("There has been an error in logging in for user {}!", username, e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }

    @Override
    public CompleteResponse<Object> logout(String username) {
        try {
            // Check if the user exists
            Optional<User> userOptional = userRepository.findByUsernameAndActive(username, true);
            if (userOptional.isEmpty()) {
                log.error("User {} not found to log out!", username);
                throw new BusinessException(USER_NOT_FOUND, LOGOUT.name());
            }
            // Revoke JWT and session token
            tokenServiceImpl.revokeSessionTokens(username);
            // Clear security context
            SecurityContextHolder.clearContext();
            log.info("User {} logged out successfully!", username);
            return getCompleteResponse(errorCodeRepository, LOGOUT_SUCCESS, LOGOUT.name(), null);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("There has been an error in logging out user {}!", username, e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, LOGOUT.name());
        }
    }
}