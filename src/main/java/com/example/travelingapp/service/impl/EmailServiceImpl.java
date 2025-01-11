package com.example.travelingapp.service.impl;

import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.example.travelingapp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.util.Common.getConfigValue;

@Service
@Log4j2
public class EmailServiceImpl implements EmailService {
    private final JavaMailSenderImpl mailSender;
    private final ConfigurationRepository configurationRepository;

    public EmailServiceImpl(JavaMailSender mailSender, ConfigurationRepository configurationRepository) {
        this.mailSender = (JavaMailSenderImpl) mailSender;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public void sendEmail(String senderEmail, String receiverEmail, String emailSubject, String emailContent) {
        try {
            //Session session = OAuth2Authenticator.getOAuth2Session(mailSender, configurationRepository);
            //MimeMessage message = new MimeMessage(session);

            MimeMessage message = mailSender.createMimeMessage();
            // Refresh access token before sending email
            String latestAccessToken = getConfigValue(EMAIL_ACCESS_TOKEN_CONFIG, configurationRepository, EMAIL.name());
            mailSender.setPassword(latestAccessToken);

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(receiverEmail);
            helper.setSubject(emailSubject);
            helper.setText(emailContent, true);
            helper.setFrom(senderEmail);

            //    Transport.send(message);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email!", e);
            throw new BusinessException(EMAIL_SENT_FAIL, EMAIL.name());
        } catch (Exception e) {
            log.error("There has been an error in sending email!", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, EMAIL.name());
        }
    }
}
