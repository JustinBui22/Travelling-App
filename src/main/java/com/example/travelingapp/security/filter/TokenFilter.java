package com.example.travelingapp.security.filter;

import com.example.travelingapp.entity.User;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.UserRepository;
import com.example.travelingapp.service.impl.TokenServiceImpl;
import com.example.travelingapp.response_template.CompleteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Arrays;

import org.springframework.web.filter.OncePerRequestFilter;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.response_template.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.Common.getNonAuthenticatedUrls;
import static com.example.travelingapp.util.DataConverter.convertStringToLong;

@Log4j2
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenServiceImpl;
    private final ConfigurationRepository configurationRepository;
    private final UserRepository userRepository;
    private final ErrorCodeRepository errorCodeRepository;

    public TokenFilter(TokenServiceImpl tokenServiceImpl, ConfigurationRepository configurationRepository, UserRepository userRepository, ErrorCodeRepository errorCodeRepository) {
        this.tokenServiceImpl = tokenServiceImpl;
        this.configurationRepository = configurationRepository;
        this.userRepository = userRepository;
        this.errorCodeRepository = errorCodeRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip token validation for non-required-authenticated URLs
        if (Arrays.stream(getNonAuthenticatedUrls(configurationRepository))
                .anyMatch(nonAuthenticatedUrl -> matchesUrlPattern(nonAuthenticatedUrl, request.getRequestURI()))) {
            log.info("Skipping token validation for public URL: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        // Token validation for required-authenticated URLs
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            CompleteResponse<Object> validateTokenResponse = tokenServiceImpl.validateJwtToken(token);
            String responseCode = validateTokenResponse.getResponseBody().getCode();
            try {
                if (responseCode.equals(TOKEN_VERIFY_SUCCESS.getCode())) {
                    handleSuccessfulTokenValidation(request, response, filterChain, validateTokenResponse);
                } else {
                    log.warn("Token validation failed for reason: {}", responseCode);
                    handleTokenValidationFailure(responseCode);
                }
            } catch (BusinessException e) {
                log.error("Business exception occurred: {}", e.getMessage(), e);
                handleBusinessException(response, e);
            } catch (Exception e) {
                log.error("There has been an error in {}!", this.getClass(), e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private boolean matchesUrlPattern(String pattern, String requestURI) {
        return requestURI.equals(pattern) || requestURI.matches(pattern.replace("**", ".*"));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void handleSuccessfulTokenValidation(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, CompleteResponse<Object> validateTokenResponse)
            throws ServletException, IOException {
        // Populate SecurityContext with authenticated user
        Claims claims = (Claims) validateTokenResponse.getResponseBody().getBody();
        User user = userRepository.findByUsernameAndStatus(claims.getSubject(), true).get();
        String userName = user.getUsername();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, userName, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Validate session token globally (fallback mechanism)
        String sessionToken = request.getHeader("Session-Token");
        if (sessionToken == null || !tokenServiceImpl.isSessionTokenValid(userName, sessionToken)) {
            log.error("Invalid session token for user: {}", userName);
            throw new BusinessException(SESSION_TOKEN_INVALID, Token.name());
        }
        // Check if the token is nearing expiration (fallback mechanism)
        long currentTokenTimeLeft = claims.getExpiration().getTime() - System.currentTimeMillis();
        long requiredTokenRefreshTime = configurationRepository.findByConfigCode(CURRENT_TOKEN_TIME_LEFT.name())
                .map(configuration -> convertStringToLong(configuration.getConfigValue()))
                .orElseGet(() -> {
                    log.info("There is no config value for {}", CURRENT_TOKEN_TIME_LEFT.name());
                    return 300000L; // default value of 5 minutes
                });
        if (currentTokenTimeLeft < requiredTokenRefreshTime) {
            String refreshedToken = (String) (tokenServiceImpl.generateJwtToken((String) SecurityContextHolder.getContext().getAuthentication().getCredentials()).getResponseBody().getBody());
            response.setHeader("X-Refresh-Token", "Bearer " + refreshedToken); // Send new token in response
        }
        filterChain.doFilter(request, response);  // Allow the request to proceed
    }

    private void handleTokenValidationFailure(String responseCode) {
        //    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (responseCode.equals(USER_NOT_FOUND.getCode())) {
            log.error("No user found as {}", SecurityContextHolder.getContext().getAuthentication().getCredentials());
            throw new BusinessException(USER_NOT_FOUND, Token.name());
        } else if (responseCode.equals(TOKEN_EXPIRE.getCode())) {
            log.error("Token expires!");
            throw new BusinessException(TOKEN_EXPIRE, Token.name());
        } else {
            log.error("Token verification failed!");
            throw new BusinessException(TOKEN_VERIFY_FAIL, Token.name());
        }
    }

    private void handleBusinessException(HttpServletResponse response, BusinessException ex) throws IOException {
        CompleteResponse<Object> result = getCompleteResponse(errorCodeRepository, ex.getErrorCodeEnum(), ex.getFlow(), null);
        response.setStatus(HttpStatusCode.valueOf(result.getHttpCode()).value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(result.getResponseBody()));
    }
}

