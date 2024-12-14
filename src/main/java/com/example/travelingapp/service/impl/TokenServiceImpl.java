package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.entity.User;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.UserRepository;
import com.example.travelingapp.service.TokenService;
import com.example.travelingapp.util.CompleteResponse;
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
import java.util.Objects;
import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.util.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.common.DataConverter.convertStringToLong;
import static com.example.travelingapp.util.common.ErrorCodeResolver.resolveErrorCode;

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
    public CompleteResponse<Object> generateToken(String phoneNumber) {
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

        String errorCode = Optional.of(token)
                .filter(t -> !t.isEmpty()) // Check if token is not empty
                .map(t -> resolveErrorCode(errorCodeRepository, TOKEN_GENERATE_SUCCESS))
                .orElseGet(() -> {
                    log.error("There is an error generating token!");
                    return resolveErrorCode(errorCodeRepository, TOKEN_GENERATE_FAIL);
                });
        ErrorCodeEnum errorCodeEnum = Objects.equals(errorCode, TOKEN_GENERATE_SUCCESS.getCode()) ? TOKEN_GENERATE_SUCCESS : TOKEN_GENERATE_FAIL;
        return getCompleteResponse(errorCodeRepository, errorCode, errorCodeEnum.name(), Token.name(), token);
    }

    // Validate the token and extract the phone number
    public CompleteResponse<Object> validateToken(String token) {
        log.info("Start validating token!");
        String phoneNumber;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token) // This validates the token
                    .getBody();
            phoneNumber = claims.getSubject();

            // Validate if the token's user exists
            log.info("Start checking if user {} is registered!", phoneNumber);
            Optional<User> userOptional = userRepository.findByPhoneNumberAndStatus(phoneNumber, true);
            if (userOptional.isEmpty()) {
                log.info("There is no user with phone number {}", phoneNumber);
                getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, USER_NOT_FOUND), Token.name());
            }
        } catch (ExpiredJwtException e) {
            return getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_EXPIRE), Token.name());
        } catch (Exception e) {
            return getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_VERIFY_FAIL), Token.name());
        }

        log.info("The token is valid for userID {}", phoneNumber);
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Current user: {}", userDetails.getPhoneNumber());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_VERIFY_SUCCESS), TOKEN_VERIFY_SUCCESS.name(), Token.name(), phoneNumber);
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

}
