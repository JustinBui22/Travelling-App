package com.example.travelingapp.security.oauth2;

import com.example.travelingapp.entity.ConfigurationEntity;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.INTERNAL_SERVER_ERROR;
import static com.example.travelingapp.util.Common.getConfigValue;

@Log4j2
public class GoogleOAuthHelper {
    private final ConfigurationRepository configurationRepository;

    public GoogleOAuthHelper(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public String refreshOAuthAccessToken(String refreshToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare request data
            String clientId = getConfigValue(EMAIL_CLIENT_ID.name(), configurationRepository, OTP.name());
            String clientSecret = getConfigValue(EMAIL_CLIENT_SECRET.name(), configurationRepository, OTP.name());
            Map<String, String> requestData = new HashMap<>();
            requestData.put("client_id", clientId);
            requestData.put("client_secret", clientSecret);
            requestData.put("refresh_token", refreshToken);
            requestData.put("grant_type", "refresh_token");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestData, headers);

            // Send POST request
            String tokenUrl = getConfigValue(EMAIL_TOKEN_URL, configurationRepository, "https://oauth2.googleapis.com/token");
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            String newAccessToken = jsonNode.get("access_token").asText();
            log.info("New access token received: {}", newAccessToken);
            return newAccessToken;
        } catch (Exception e) {
            log.error("Failed to refresh OAuth2 token", e);
            throw new BusinessException(INTERNAL_SERVER_ERROR, OTP.name());
        }
    }

    @Scheduled(fixedRate = 3500000) // Refresh every ~1 hour
    public void refreshOAuthToken() {
        try {
            String refreshToken = getConfigValue(EMAIL_REFRESH_TOKEN, configurationRepository, OTP.name());
            String newAccessToken = refreshOAuthAccessToken(refreshToken);

            // Store new token
            ConfigurationEntity config = configurationRepository.findByConfigCode(EMAIL_ACCESS_TOKEN_CONFIG.name())
                    .orElse(new ConfigurationEntity(EMAIL_ACCESS_TOKEN_CONFIG.name(), newAccessToken));
            config.setConfigValue(newAccessToken);
            configurationRepository.save(config);
            log.info("OAuth2 token refreshed successfully.");
        } catch (Exception e) {
            log.error("Error refreshing OAuth2 token", e);
        }
    }
}

