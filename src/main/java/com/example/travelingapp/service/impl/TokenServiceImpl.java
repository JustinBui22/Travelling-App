package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.UserRepository;
import com.example.travelingapp.service.TokenService;
import com.example.travelingapp.response_template.CompleteResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

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

    public TokenServiceImpl(ConfigurationRepository configurationRepository, ErrorCodeRepository errorCodeRepository, UserRepository userRepository) {
        this.configurationRepository = configurationRepository;
        this.errorCodeRepository = errorCodeRepository;
        this.userRepository = userRepository;
    }

    // Generate a Bearer Token based on the user's phone number
    public CompleteResponse<Object> generateJwtToken(String phoneNumber) {
        log.info("Start generating token!");
        Optional<Configuration> expirationTimeConfigOptional = configurationRepository.findByConfigCode(TOKEN_EXPIRATION_TIME.name());
        long expirationTime = expirationTimeConfigOptional
                .map(configuration -> convertStringToLong(configuration.getConfigValue()))
                .orElseGet(() -> {
                    log.info("There is no config value for {}", TOKEN_EXPIRATION_TIME.name());
                    return 300000L; // default value of 5 minutes
                });

        String token = Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSecretKey()) // Specify the signing algorithm
                .compact();

        ErrorCodeEnum errorCodeEnum = Optional.of(token)
                .filter(t -> !t.isEmpty()) // Check if token is not empty
                .map(t -> TOKEN_GENERATE_SUCCESS)
                .orElseGet(() -> {
                    log.error("There is an error generating token!");
                    return TOKEN_GENERATE_FAIL;
                });

        return getCompleteResponse(errorCodeRepository, errorCodeEnum, Token.name(), token);
    }

    @Override
    public CompleteResponse<Object> refreshJwtToken(String authorizationHeader) {
        log.info("Start refreshing token!");
        // Checking if the request has the authorization header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_FAIL, Token.name(), null);
        }
        String token = authorizationHeader.substring(7);
        String validateTokenCode = validateJwtToken(token).getResponseBody().getCode();
        if (validateTokenCode.equals(TOKEN_VERIFY_SUCCESS.getCode())) {
            log.info("Token validated successfully!");
            SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            return generateJwtToken(phoneNumber);
        }
        log.warn("Token validation failed for reason: {}", validateTokenCode);
        if (validateTokenCode.equals(USER_NOT_FOUND.getCode())) {
            return getCompleteResponse(errorCodeRepository, USER_NOT_FOUND, Token.name(), null);
        } else if (validateTokenCode.equals(TOKEN_EXPIRE.getCode())) {
            return getCompleteResponse(errorCodeRepository, TOKEN_EXPIRE, Token.name(), null);
        } else {
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_FAIL, Token.name(), null);
        }
    }

    // Validate the token and extract the phone number
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public CompleteResponse<Object> validateJwtToken(String token) {
        log.info("Start validating token!");
        String phoneNumber;
        Optional<User> userOptional;
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token) // This validates the token
                    .getBody();
            phoneNumber = claims.getSubject();

            // Validate if the token's user exists
            log.info("Start checking if user {} is registered!", phoneNumber);
            userOptional = userRepository.findByPhoneNumberAndStatus(phoneNumber, true);
            if (userOptional.isEmpty()) {
                log.info("There is no user with phone number {}", phoneNumber);
                getCompleteResponse(errorCodeRepository, USER_NOT_FOUND, Token.name(), null);
            }
        } catch (ExpiredJwtException e) {
            log.error("Token expires!");
            throw new BusinessException(ErrorCodeEnum.TOKEN_EXPIRE, Token.name());
        } catch (Exception e) {
            log.error("Token verification failed!");
            throw new BusinessException(ErrorCodeEnum.TOKEN_VERIFY_FAIL, Token.name());
        }

        log.info("The token is valid for userID {}", phoneNumber);
        // Populate SecurityContext with authenticated user
        User user = userOptional.get();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPhoneNumber(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_SUCCESS, Token.name(), claims);
    }

    // Method to get the SECRET key dynamically
    private SecretKey getSecretKey() {
        Optional<Configuration> secretConfig = configurationRepository.findByConfigCode(SECRET_KEY_CONFIG.name());
        String secret = secretConfig
                .map(Configuration::getConfigValue)
                .orElseThrow(() -> {
                    log.error("Secret key configuration is missing!");
                    return new RuntimeException();
                });

        // Ensure the key is at least 256 bits for HS512
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            log.error("Secret key must be at least 256 bits (32 bytes) for HS512!");
            throw new RuntimeException();
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public void storeSessionToken(String userName, String token) {

    }

    @Override
    public CompleteResponse<Object> getSessionToken(String userName) {
        return null;
    }

    @Override
    public void invalidateSessionToken(String userName) {

    }

    @Override
    public boolean isSessionTokenValid(String userName, String token) {
        return false;
    }

}
