package com.example.travelingapp.security.filter;

import com.example.travelingapp.entity.User;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.repository.UserRepository;
import com.example.travelingapp.service.impl.TokenServiceImpl;
import com.example.travelingapp.response_template.CompleteResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
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
import static com.example.travelingapp.util.DataConverter.toJson;

@Log4j2
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenServiceImpl;
    private final ErrorCodeRepository errorCodeRepository;
    private final ConfigurationRepository configurationRepository;
    private final UserRepository userRepository;

    public TokenFilter(TokenServiceImpl tokenServiceImpl, ErrorCodeRepository errorCodeRepository, ConfigurationRepository configurationRepository, UserRepository userRepository) {
        this.tokenServiceImpl = tokenServiceImpl;
        this.errorCodeRepository = errorCodeRepository;
        this.configurationRepository = configurationRepository;
        this.userRepository = userRepository;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip token validation for non-authenticated URLs
        if (Arrays.stream(getNonAuthenticatedUrls(configurationRepository))
                .anyMatch(nonAuthenticatedUrl -> request.getRequestURI().equals(nonAuthenticatedUrl))) {
            log.info("Skipping token validation for public URL: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        // Token validation for authenticated URLs
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            CompleteResponse<Object> validateTokenResponse = tokenServiceImpl.validateToken(token);
            String responseCode = validateTokenResponse.getResponseBody().getCode();
            try {
                if (responseCode.equals(TOKEN_VERIFY_SUCCESS.getCode())) {
                    // Populate SecurityContext with authenticated user
                    Claims claims = (Claims) validateTokenResponse.getResponseBody().getBody();
                    User user = userRepository.findByPhoneNumberAndStatus(claims.getSubject(), true).get();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            user, user.getPhoneNumber(), user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Check if the token is nearing expiration
                    long currentTokenTimeLeft = claims.getExpiration().getTime() - System.currentTimeMillis();
                    long requiredTokenRefreshTime = configurationRepository.findByConfigCode(CURRENT_TOKEN_TIME_LEFT.name())
                            .map(configuration -> convertStringToLong(configuration.getConfigValue()))
                            .orElseGet(() -> {
                                log.info("There is no config value for {}", CURRENT_TOKEN_TIME_LEFT.name());
                                return 300000L; // default value of 5 minutes
                            });
                    if (currentTokenTimeLeft < requiredTokenRefreshTime) {
                        String refreshedToken = (String) (tokenServiceImpl.generateToken((String) SecurityContextHolder.getContext().getAuthentication().getCredentials()).getResponseBody().getBody());
                        response.setHeader("Authorization", "Bearer " + refreshedToken); // Send new token in response
                    }
                    filterChain.doFilter(request, response);  // Allow the request to proceed
                    return;
                }
                if (responseCode.equals(USER_NOT_FOUND.getCode())) {
                    response.getWriter().print(toJson(getCompleteResponse(errorCodeRepository, USER_NOT_FOUND, Token.name(), null)));
                } else if (responseCode.equals(TOKEN_EXPIRE.getCode())) {
                    response.getWriter().print(toJson(getCompleteResponse(errorCodeRepository, TOKEN_EXPIRE, Token.name(), null)));
                } else {
                    response.getWriter().print(toJson(getCompleteResponse(errorCodeRepository, TOKEN_VERIFY_FAIL, Token.name(), null)));
                }
                log.warn("Token validation failed for reason: {}", responseCode);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.flushBuffer();
            } catch (Exception e) {
                log.error("There has been an error in {}!", this.getClass(), e);
                throw new BusinessException(INTERNAL_SERVER_ERROR, Common.name());
            }
            // For request that need authorization but does not have it
            filterChain.doFilter(request, response);
        }
    }

}

