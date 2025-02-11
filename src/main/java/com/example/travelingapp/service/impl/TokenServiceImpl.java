package com.example.travelingapp.service.impl;

import com.example.travelingapp.entity.SessionTokenStoreEntity;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static com.example.travelingapp.util.Common.getConfigValue;
import static com.example.travelingapp.validator.InputValidator.validatePhoneForm;
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
    public CompleteResponse<Object> generateJwtToken(String username) {
        try {
            log.info("Start generating token!");
            long expirationTime = convertStringToLong(getConfigValue(TOKEN_EXPIRATION_TIME.name(), configurationRepository, "300000L"));
            // Backup mechanism to check if username is phone number
            if (validatePhoneForm(username, configurationRepository.findByConfigCode(PHONE_VN_PATTERN.name()))) {
                log.error("Input format is invalid for token generation!");
                throw new BusinessException(INPUT_FORMAT_INVALID, TOKEN.name());
            }
            User user = userRepository.findByUsernameAndActive(username, true).orElseGet(() -> {
                log.error("There is user as {}", username);
                throw new BusinessException(USER_NOT_FOUND, COMMON.name());
            });
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .claim("roles", user.getAuthorities())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSecretKey()) // Specify the signing algorithm
                    .compact();
            ErrorCodeEnum errorCodeEnum = Optional.of(token).filter(t -> !t.isEmpty()) // Check if token is not empty
                    .map(t -> TOKEN_GENERATE_SUCCESS).orElseGet(() -> {
                        log.error("There is an error generating token!");
                        return TOKEN_GENERATE_FAIL;
                    });
            return getCompleteResponse(errorCodeRepository, errorCodeEnum, TOKEN.name(), token);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Jwt token generated failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }

    @Override
    public CompleteResponse<Object> refreshJwtToken(String authorizationHeader, String sessionTokenHeader, String userName) {
        log.info("Start refreshing token!");
        String username = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return generateJwtToken(username);
    }

    // Validate the token and extract the phone number
    public CompleteResponse<Object> validateJwtToken(String token) {
        log.info("Start validating token!");
        String username;
        Claims claims;
        Optional<User> userOptional;
        try {
            claims = Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token) // This validates the token
                    .getBody();
            username = claims.getSubject();
            // Validate if the token's user exists
            log.info("Start checking if user {} is registered!", username);
            userOptional = userRepository.findByUsernameAndActive(username, true);
            if (userOptional.isEmpty()) {
                log.error("There is no user as {}", username);
                return getCompleteResponse(errorCodeRepository, USER_NOT_FOUND, TOKEN.name(), null);
            }
            log.info("The token is valid for user {}", username);
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_SUCCESS, TOKEN.name(), claims);
        } catch (ExpiredJwtException e) {
            log.error("Token expires!");
            return getCompleteResponse(errorCodeRepository, TOKEN_EXPIRE, TOKEN.name(), null);
        } catch (Exception e) {
            log.error("There is an error in validating Jwt token!");
            return getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_FAIL, TOKEN.name(), null);
        }
    }

    // Method to get the SECRET key dynamically
    private SecretKey getSecretKey() {
        String secret = getConfigValue(SECRET_KEY_CONFIG, configurationRepository, TOKEN.name());
        // Ensure the key is at least 256 bits for HS512
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            log.error("Secret key must be at least 256 bits (32 bytes) for HS512!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private void storeSessionToken(String userName, String token) {
        try {
            String sessionTokenId = UUID.randomUUID().toString();
            SessionTokenStoreEntity newToken = new SessionTokenStoreEntity(userName, passwordEncoder.encode(token), sessionTokenId, LocalDate.now());
            sessionTokenRepository.save(newToken);
            log.info("Session token for user {} stored successfully!", userName);
        } catch (Exception e) {
            log.error("Session token stored failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }

    @Override
    public CompleteResponse<Object> generateSessionToken(String userName) {
        try {
            String sessionToken = Jwts.builder().setSubject(userName).setIssuedAt(new Date()).signWith(getSecretKey()).compact();
            storeSessionToken(userName, sessionToken);
            return getCompleteResponse(errorCodeRepository, TOKEN_GENERATE_SUCCESS, TOKEN.name(), sessionToken);
        } catch (Exception e) {
            log.error("Session token generated failed!");
            throw new BusinessException(INTERNAL_SERVER_ERROR, COMMON.name());
        }
    }

    @Override
    public boolean isSessionTokenInvalid(String username, String token) {
        isExceedMaxAllowedSessions(username);
        // Check if the token session is correct
        List<SessionTokenStoreEntity> activeSessionList = sessionTokenRepository.findAllByUserName(username);
        for (SessionTokenStoreEntity sessionTokenStoreEntity : activeSessionList) {
            if (passwordEncoder.matches(token, sessionTokenStoreEntity.getSessionToken())) {
                log.info("User {} session token is valid!", username);
                return false;
            }
        }
        log.info("User {} session token is invalid!", username);
        return true;
    }

    public void isExceedMaxAllowedSessions(String username) {
        int maxSessionConfig = Integer.parseInt(getConfigValue(MAX_ALLOWED_SESSIONS.name(), configurationRepository, "3"));
        List<SessionTokenStoreEntity> activeSessionList = sessionTokenRepository.findAllByUserName(username);
        // Check if the user has exceeded maxed number of active sessions
        if (activeSessionList.size() >= maxSessionConfig) {
            log.info("Exceeding max allowed number of active sessions for user {}", username);
            throw new BusinessException(MAX_SESSIONS_REACHED, LOGIN.name());
        }
    }

    @Override
    public void revokeSessionTokens(String username) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String sessionToken = Objects.requireNonNull(attributes).getRequest().getHeader("Session-Token");

        Optional<SessionTokenStoreEntity> tokenOptional = sessionTokenRepository.findByUserNameAndSessionToken(username, sessionToken);
        if (tokenOptional.isPresent()) {
            sessionTokenRepository.delete(tokenOptional.get());
            sessionTokenRepository.flush();
            log.info("Session token revoked for user {} successfully!", username);
        } else {
            log.error("No existing session token to be revoked for user {}", username);
            throw new BusinessException(SESSION_TOKEN_INVALID, TOKEN.name());
        }
    }
}
