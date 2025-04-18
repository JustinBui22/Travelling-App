package com.example.travelingapp.security.oauth2;

import com.example.travelingapp.entity.ConfigurationEntity;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.INTERNAL_SERVER_ERROR;
import static com.example.travelingapp.util.Common.getConfigValue;

@Log4j2
@Component
@EnableScheduling
public class GoogleOAuthHelper implements SchedulingConfigurer {
    private final ConfigurationRepository configurationRepository;
    private ScheduledExecutorService scheduler;

    public GoogleOAuthHelper(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public void refreshOAuthToken() {
        try {
            String refreshToken = getConfigValue(EMAIL_REFRESH_TOKEN, configurationRepository, OTP.name());

            RestTemplate restTemplate = new RestTemplate();
            // Prepare request data
            String clientId = getConfigValue(EMAIL_CLIENT_ID.name(), configurationRepository, OTP.name());
            String clientSecret = getConfigValue(EMAIL_CLIENT_SECRET.name(), configurationRepository, OTP.name());
            LinkedMultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
            requestData.add("client_id", clientId);
            requestData.add("client_secret", clientSecret);
            requestData.add("refresh_token", refreshToken);
            requestData.add("grant_type", "refresh_token");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(requestData, headers);

            // Send POST request
            String tokenUrl = getConfigValue(EMAIL_TOKEN_URL, configurationRepository, "https://oauth2.googleapis.com/token");
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            // Parse JSON response
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                String newAccessToken = jsonNode.get("access_token").asText();
                // get new refresh token if received
                String newRefreshToken = jsonNode.findValue("refresh_token") == null ? null : jsonNode.get("refresh_token").asText();
                if (newRefreshToken != null && !newRefreshToken.equals(refreshToken)) {
                    ConfigurationEntity oauthRefreshTokenConfig = configurationRepository.findByConfigCode(EMAIL_REFRESH_TOKEN.name())
                            .orElse(new ConfigurationEntity(EMAIL_REFRESH_TOKEN.name(), newRefreshToken, LocalDate.now()));
                    oauthRefreshTokenConfig.setConfigValue(newRefreshToken);
                    log.info("New refresh token for Oauth2 received!");
                }
                log.info("New access token for Oauth2 received!");
                // Store new access token
                ConfigurationEntity config = configurationRepository.findByConfigCode(EMAIL_ACCESS_TOKEN_CONFIG.name())
                        .orElse(new ConfigurationEntity(EMAIL_ACCESS_TOKEN_CONFIG.name(), newAccessToken, LocalDate.now()));
                config.setConfigValue(newAccessToken);
                config.setModifiedDate(LocalDate.now());
                configurationRepository.save(config);
                log.info("OAuth2 token refreshed successfully.");
            }
            else {
                log.error(" Error code: {} - Failed to refresh OAuth2 token!", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Exception in refreshing OAuth2 token!", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, OTP.name());
        }
    }

    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        long refreshRate = Long.parseLong(getConfigValue(EMAIL_REFRESH_ACCESS_TOKEN_RATE.name(), configurationRepository, "3500000"));
        // Initialize scheduler only once and store it
        // This executor won't be automatically shut down by Spring.
        // Storing it as a field to stop or manage your scheduled tasks
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        // Schedule task using newSingleThreadScheduledExecutor
        scheduler.scheduleAtFixedRate(this::refreshOAuthToken, 0, refreshRate, TimeUnit.MILLISECONDS);
        log.info("OAuth token refresh scheduled every {} milliseconds.", refreshRate);
    }
}

