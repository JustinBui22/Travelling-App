package com.example.travelingapp.security.filter;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String usrName = request.getHeader(“userName”);
//        log.info("Successfully authenticated user  " +
//                usrName);
//        filterChain.doFilter(request, response);
//    }
}
