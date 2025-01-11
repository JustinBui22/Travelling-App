package com.example.travelingapp.security.oauth2;

import com.example.travelingapp.repository.ConfigurationRepository;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.util.Common.getConfigValue;

public class OAuth2Authenticator extends Authenticator {
    private final String accessToken;
    private final ConfigurationRepository configurationRepository;

    public OAuth2Authenticator(String accessToken, ConfigurationRepository configurationRepository) {
        this.accessToken = accessToken;
        this.configurationRepository = configurationRepository;
    }

//    @Override
//    protected PasswordAuthentication getPasswordAuthentication() {
//        String emailAddress = getConfigValue(EMAIL_ADDRESS_CONFIG.name(), configurationRepository, EMAIL.name());
//        return new PasswordAuthentication(emailAddress, accessToken);
//    }

    public static Session getOAuth2Session(JavaMailSenderImpl mailSender, ConfigurationRepository configurationRepository) {
        String accessToken = getConfigValue(EMAIL_ACCESS_TOKEN_CONFIG.name(), configurationRepository, EMAIL.name());
        return Session.getInstance(mailSender.getJavaMailProperties(), new OAuth2Authenticator(accessToken, configurationRepository));
    }
}
