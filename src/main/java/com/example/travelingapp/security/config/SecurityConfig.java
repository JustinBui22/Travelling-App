package com.example.travelingapp.security.config;

import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.security.filter.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

import static com.example.travelingapp.util.Common.getNonAuthenticatedUrls;

@Configuration
public class SecurityConfig {

    private final ConfigurationRepository configurationRepository;

    public SecurityConfig(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter tokenFilter, Environment environment) throws Exception {
        String contextPath = environment.getProperty("server.servlet.context-path", "/The-Project");
        // Configure HttpSecurity with dynamic non-authenticated URLs
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                    // Permit the non-authenticated URLs dynamically
                    Arrays.stream(getNonAuthenticatedUrls(configurationRepository))
                            .forEach(url -> auth.requestMatchers(url.replaceFirst("^" + contextPath, "")).permitAll());
                    auth.anyRequest().authenticated();
                })
//                .formLogin().disable()
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless session management for JWT
//                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class) // Add the token filter
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/welcome", true)
                        .failureUrl("/login?error")
                );
        return http.build();
    }

    @Bean
    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }
}

