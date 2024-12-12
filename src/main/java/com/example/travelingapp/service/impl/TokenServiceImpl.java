package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.service.TokenService;
import com.example.travelingapp.util.CompleteResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import lombok.extern.log4j.Log4j2;
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

    public TokenServiceImpl(ConfigurationRepository configurationRepository, ErrorCodeRepository errorCodeRepository) {
        this.configurationRepository = configurationRepository;
        this.errorCodeRepository = errorCodeRepository;
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
                .signWith(getSecretKey())
                .compact();

        String errorCode = Optional.of(token)
                .filter(t -> !t.isEmpty()) // Check if token is not empty
                .map(t -> resolveErrorCode(errorCodeRepository, TOKEN_GENERATE_SUCCESS))
                .orElseGet(() -> {
                    log.error("There is an error generating token!"); // Log the error
                    return resolveErrorCode(errorCodeRepository, TOKEN_GENERATE_FAIL); // Return failure error code
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
        } catch (Exception e) {
            log.error("The token is invalid! {}", e.getMessage());
            return getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_VERIFY_FAIL), Token.name());
        }
        log.info("The token is valid for userID {}", phoneNumber);
        String errorCode = resolveErrorCode(errorCodeRepository, TOKEN_VERIFY_SUCCESS);
        ErrorCodeEnum errorCodeEnum = Objects.equals(errorCode, TOKEN_VERIFY_SUCCESS.getCode()) ? TOKEN_VERIFY_SUCCESS : TOKEN_VERIFY_FAIL;
        return getCompleteResponse(errorCodeRepository, errorCode, errorCodeEnum.name(), Token.name(), phoneNumber);
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
