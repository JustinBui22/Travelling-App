package com.example.travelingapp.security.config;

import com.example.travelingapp.repository.ConfigurationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.util.Common.getConfigValue;

@Log4j2
@Configuration
public class MailConfig {
    private final ConfigurationRepository configurationRepository;

    public MailConfig(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(getConfigValue(EMAIL_HOST_CONFIG.name(), configurationRepository, "smtp.gmail.com"));
        mailSender.setPort(Integer.parseInt(getConfigValue(EMAIL_PORT_CONFIG.name(), configurationRepository, "587")));
        mailSender.setUsername(getConfigValue(EMAIL_ADDRESS_CONFIG.name(), configurationRepository, "needforspeed160899@gmail.com"));
        mailSender.setPassword(getConfigValue(EMAIL_ACCESS_TOKEN_CONFIG.name(), configurationRepository, "default_access_token"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        // OAuth2 properties
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");

        return mailSender;
    }
}
