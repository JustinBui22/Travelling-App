package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.service.TokenService;
import com.example.travelingapp.util.CompleteResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.util.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.common.DataConverter.convertStringToLong;
import static com.example.travelingapp.util.common.ErrorCodeResolver.resolveErrorCode;

@Log4j2
@Service
public class TokenServiceImpl implements TokenService {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final ConfigurationRepository configurationRepository;
    private final ErrorCodeRepository errorCodeRepository;

    public TokenServiceImpl(ConfigurationRepository configurationRepository, ErrorCodeRepository errorCodeRepository) {
        this.configurationRepository = configurationRepository;
        this.errorCodeRepository = errorCodeRepository;
    }

    // Generate a Bearer Token based on the user's phone number
    public CompleteResponse<Object> generateToken(String phoneNumber) {
        log.info("Start generating token!");
        Optional<Configuration> expirationTimeConfigOptional = configurationRepository.findByConfigCode(EXPIRATION_TIME.name());
        long expirationTime = expirationTimeConfigOptional
                .map(configuration -> convertStringToLong(configuration.getConfigValue()))
                .orElseGet(() -> {
                    log.info("There is no config value for {}", EXPIRATION_TIME.name());
                    return 300000L; // default value of 5 minutes
                });

        String token = Jwts.builder()
                .setSubject(phoneNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)
                .compact();

        String errorCode = Optional.of(token)
                .filter(t -> !t.equals("Bearer ")) // Check if token is not empty
                .map(t -> resolveErrorCode(errorCodeRepository, TOKEN_GENERATE_SUCCESS))
                .orElseGet(() -> {
                    log.info("There is an error generating token!"); // Log the error
                    return resolveErrorCode(errorCodeRepository, TOKEN_GENERATE_FAIL); // Return failure error code
                });

        return getCompleteResponse(errorCodeRepository, errorCode, Token.name(), token);
    }

    // Validate the token and extract the phone number
    public CompleteResponse<Object> validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token).getBody(); // This validates the token
        } catch (Exception e) {
            log.info("The token is invalid! {}", e.getMessage());
            return null; // Invalid token
        }
        return getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_VERIFY_SUCCESS), Token.name());
    }
}
