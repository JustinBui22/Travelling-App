package com.example.travelingapp.security.filter;

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

import static com.example.travelingapp.enums.ErrorCodeEnum.TOKEN_GENERATE_SUCCESS;

@Log4j2
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final TokenServiceImpl tokenServiceImpl;

    public TokenFilter(TokenServiceImpl tokenServiceImpl) {
        this.tokenServiceImpl = tokenServiceImpl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Start validating token!");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (tokenServiceImpl.validateToken(token).getResponseBody().getCode().equals(TOKEN_GENERATE_SUCCESS.getCode())) {
                // Add the phone number to request attributes for further use
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Token valid");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

