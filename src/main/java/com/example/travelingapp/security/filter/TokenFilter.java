package com.example.travelingapp.security.filter;

import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.service.impl.TokenServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;


import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import static com.example.travelingapp.enums.CommonEnum.Token;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.util.CompleteResponse.getCompleteResponse;
import static com.example.travelingapp.util.common.DataConverter.toJson;
import static com.example.travelingapp.util.common.ErrorCodeResolver.resolveErrorCode;

@Log4j2
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenServiceImpl;
    private final ErrorCodeRepository errorCodeRepository;

    public TokenFilter(TokenServiceImpl tokenServiceImpl, ErrorCodeRepository errorCodeRepository) {
        this.tokenServiceImpl = tokenServiceImpl;
        this.errorCodeRepository = errorCodeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Start validating token!");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String responseCode = tokenServiceImpl.validateToken(token).getResponseBody().getCode();
            if (responseCode.equals(TOKEN_VERIFY_SUCCESS.getCode())) {
                // Add the phone number to request attributes for further use
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (responseCode.equals(USER_NOT_FOUND.getCode())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(toJson(getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, USER_NOT_FOUND), Token.name())));
                return;
            } else if (responseCode.equals(TOKEN_EXPIRE.getCode())) {
                log.warn("Session expired!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(toJson(getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_EXPIRE), Token.name())));
                return;
            } else {
                log.warn("Invalid token!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print(toJson(getCompleteResponse(errorCodeRepository, resolveErrorCode(errorCodeRepository, TOKEN_VERIFY_FAIL), Token.name())));
                return;
            }
        }
        log.info("There is no selected authentication for api {}!", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

}

