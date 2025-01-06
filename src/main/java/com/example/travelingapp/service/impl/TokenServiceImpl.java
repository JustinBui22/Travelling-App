package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.entity.SessionTokenStore;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.SessionTokenRepository;
import com.example.travelingapp.repository.UserRepository;
import com.example.travelingapp.response_template.CompleteResponse;
import com.example.travelingapp.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.example.travelingapp.Validator.InputValidator.validatePhoneForm;
import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.response_template.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.DataConverter.convertStringToLong;

@Log4j2
@Service
public class TokenServiceImpl implements TokenService {
    private final ConfigurationRepository configurationRepository;
    private final ErrorCodeRepository errorCodeRepository;
    private final UserRepository userRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public TokenServiceImpl(ConfigurationRepository configurationRepository, ErrorCodeRepository errorCodeRepository, UserRepository userRepository, SessionTokenRepository sessionTokenRepository, PasswordEncoder passwordEncoder) {
        this.configurationRepository = configurationRepository;
        this.errorCodeRepository = errorCodeRepository;
        this.userRepository = userRepository;
        this.sessionTokenRepository = sessionTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Generate a Bearer Token based on the username
    public CompleteResponse<Object> generateJwtToken(String subject) {
        try {
            log.info("Start generating token!");
            Optional<Configuration> expirationTimeConfigOptional = configurationRepository.findByConfigCode(TOKEN_EXPIRATION_TIME.name());
            long expirationTime = expirationTimeConfigOptional.map(configuration -> convertStringToLong(configuration.getConfigValue())).orElseGet(() -> {
                log.info("There is no config value for {}", TOKEN_EXPIRATION_TIME.name());
                return 300000L; // default value of 5 minutes
            });

            // Backup mechanism to check if username is phone number
            if (validatePhoneForm(subject, configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                log.error("Input format is invalid for token generation!");
                throw new BusinessException(INPUT_FORMAT_INVALID, Token.name());
            }
            String token = Jwts.builder().setSubject(subject).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(getSecretKey()) // Specify the signing algorithm
                    .compact();
            ErrorCodeEnum errorCodeEnum = Optional.of(token).filter(t -> !t.isEmpty()) // Check if token is not empty
                    .map(t -> TOKEN_GENERATE_SUCCESS).orElseGet(() -> {
                        log.error("There is an error generating token!");
                        return TOKEN_GENERATE_FAIL;
                    });
            return getCompleteResponse(errorCodeRepository, errorCodeEnum, Token.name(), token);
        } catch (Exception e) {
            log.error("Jwt token generated failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
    }

    @Override
    public CompleteResponse<Object> refreshJwtToken(String authorizationHeader, String sessionTokenHeader, String userName) {
        log.info("Start refreshing token!");
        // Checking if the request has the authorization header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_FAIL, Token.name(), null);
        }
        log.info("Token validated successfully!");
        String object = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return generateJwtToken(object);
    }

    // Validate the token and extract the phone number
    public CompleteResponse<Object> validateJwtToken(String token) {
        log.info("Start validating token!");
        String object;
        Claims claims;
        Optional<User> userOptional;
        try {
            claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token) // This validates the token
                    .getBody();
            object = claims.getSubject();
            // Validate if the token's user exists
            log.info("Start checking if user {} is registered!", object);
            userOptional = userRepository.findByUsernameAndStatus(object, true);
            if (userOptional.isEmpty()) {
                log.error("There is no user as {}", object);
                return getCompleteResponse(errorCodeRepository, USER_NOT_FOUND, Token.name(), null);
            }
            log.info("The token is valid for user {}", object);
            // Populate SecurityContext with authenticated user
            User user = userOptional.get();
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_SUCCESS, Token.name(), claims);
        } catch (ExpiredJwtException e) {
            log.error("Token expires!");
            return getCompleteResponse(errorCodeRepository, TOKEN_EXPIRE, Token.name(), null);
        } catch (Exception e) {
            log.error("There is an error in validating Jwt token!");
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_FAIL, Token.name(), null);
        }
    }

    // Method to get the SECRET key dynamically
    private SecretKey getSecretKey() {
        Optional<Configuration> secretConfig = configurationRepository.findByConfigCode(SECRET_KEY_CONFIG.name());
        String secret = secretConfig.map(Configuration::getConfigValue).orElseThrow(() -> {
            log.error("Secret key configuration is missing!");
            return new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        });

        // Ensure the key is at least 256 bits for HS512
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            log.error("Secret key must be at least 256 bits (32 bytes) for HS512!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private void storeSessionToken(String userName, String token) {
        try {
            String sessionTokenId = UUID.randomUUID().toString();
            SessionTokenStore newToken = new SessionTokenStore(userName, passwordEncoder.encode(token), sessionTokenId, LocalDate.now());
            sessionTokenRepository.save(newToken);
            log.info("Session token for user {} stored successfully!", userName);
        } catch (Exception e) {
            log.error("Session token stored failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
    }

    @Override
    public CompleteResponse<Object> generateSessionToken(String userName) {
        try {
            String sessionToken = Jwts.builder().setSubject(userName).setIssuedAt(new Date()).signWith(getSecretKey()).compact();
            storeSessionToken(userName, sessionToken);
            return getCompleteResponse(errorCodeRepository, TOKEN_GENERATE_SUCCESS, Token.name(), sessionToken);
        } catch (Exception e) {
            log.error("Session token generated failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
    }

    @Override
    public CompleteResponse<Object> getActiveSessionToken(String userName) {
        try {
            List<SessionTokenStore> sessionTokenList = sessionTokenRepository.findAllByUserName(userName);
            log.info("Session tokens retrieved for user {} successfully!", userName);
            return getCompleteResponse(errorCodeRepository, TOKEN_RETRIEVE_SUCCESS, Token.name(), sessionTokenList);
        } catch (Exception e) {
            log.error("Session token retrieved failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
        }
    }

    @Override
    public void invalidateOldestSessionToken(String userName) {
        Optional<SessionTokenStore> tokenOptional = sessionTokenRepository.findByUserNameOrderByCreatedDate(userName);
        if (tokenOptional.isPresent()) {
            sessionTokenRepository.delete(tokenOptional.get());
            sessionTokenRepository.flush();
            log.info("Oldest session token deleted for user {} successfully!", userName);
        } else {
            log.info("No existing session token to be invalidate for user {}", userName);
        }
    }

    @Override
    public boolean isSessionTokenValid(String username, String token) {
        isExceedMaxAllowedSessions(username);
        // Check if the token session is correct
        List<SessionTokenStore> activeSessionList = sessionTokenRepository.findAllByUserName(username);
        for (SessionTokenStore sessionTokenStore : activeSessionList) {
            if (passwordEncoder.matches(token, sessionTokenStore.getToken())) {
                log.info("User {} session token is valid!", username);
                return true;
            }
        }
        log.info("User {} session token is invalid!", username);
        return false;
    }

    public void isExceedMaxAllowedSessions(String username) {
        Optional<Configuration> maxSessionConfigOptional = configurationRepository.findByConfigCode(MAX_ALLOWED_SESSIONS.name());
        int maxSessionConfig = maxSessionConfigOptional.map(configuration -> Integer.parseInt(configuration.getConfigValue())).orElse(3);
        List<SessionTokenStore> activeSessionList = sessionTokenRepository.findAllByUserName(username);
        // Check if the user has exceeded maxed number of active sessions
        if (activeSessionList.size() >= maxSessionConfig) {
            log.info("Exceeding max allowed number of active sessions for user {}", username);
            throw new BusinessException(MAX_SESSIONS_REACHED, Login.name());
        }
    }
}
