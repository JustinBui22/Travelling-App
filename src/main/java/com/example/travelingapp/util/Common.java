package com.example.travelingapp.util;

import com.example.travelingapp.entity.ConfigurationEntity;
import com.example.travelingapp.entity.ErrorCodeEntity;
import com.example.travelingapp.enums.CommonEnum;
import com.example.travelingapp.enums.HttpStatusCodeEnum;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;

@Log4j2
public class Common {
    private Common() {
    }

    public static String[] getNonAuthenticatedUrls(ConfigurationRepository configurationRepository) {
        Optional<ConfigurationEntity> nonAuthenRequestUrlOptional = configurationRepository.findByConfigCode(NON_AUTHENTICATED_REQUEST.name());
        if (nonAuthenRequestUrlOptional.isPresent()) {
            // Clean and split the configuration value to get individual URLs
            String[] nonAuthenticatedUrls = nonAuthenRequestUrlOptional.get().getConfigValue()
                    .replaceAll("[{}]", "") // Remove curly braces
                    .split(","); // Split by commas to get individual URLs
            // Clean up each URL in the array (remove newlines, trim spaces)
            return Arrays.stream(nonAuthenticatedUrls)
                    .map(url -> url.replace("\\n", "").trim()) // Remove newline characters and trim spaces
                    .toArray(String[]::new);
        } else {
            log.warn("There is no config for {}", NON_AUTHENTICATED_REQUEST);
            return new String[0];
        }
    }

    public static HttpStatusCodeEnum getHttpFromErrorCode(ErrorCodeEntity errorCodeEntity) {
        if (errorCodeEntity == null) {
            log.info("Error code is null, returning undefined HTTP status code.");
            return UNDEFINED_HTTP_CODE.getHttpStatusCodeEnum();
        }
        try {
            // Map the HTTP code using the ErrorCode object
            return HttpStatusCodeEnum.valueOf(errorCodeEntity.getHttpCode());
        } catch (IllegalArgumentException e) {
            // Log and return default if mapping fails
            log.error("There is an error extracting http code for {}",
                    errorCodeEntity.getErrorEnum());
            throw new BusinessException(UNDEFINED_HTTP_CODE, COMMON.name());
        }
    }

    public static String getErrorCode(ErrorCodeEntity errorCodeEntity) {
        if (errorCodeEntity == null) {
            log.info("Error code is null, returning undefined error code.");
            return UNDEFINED_ERROR_CODE.getCode();
        }
        try {
            return errorCodeEntity.getErrorCode();
        } catch (IllegalArgumentException e) {
            log.error("There is an error extracting error code for {}!",
                    errorCodeEntity.getErrorEnum());
            throw new BusinessException(UNDEFINED_ERROR_CODE, COMMON.name());
        }
    }

    public static String getErrorCodeMessage(ErrorCodeEntity errorCodeEntity) {
        if (errorCodeEntity == null) {
            log.info("Error code message is null, returning undefined error code message.");
            return UNDEFINED_ERROR_CODE.getMessage();
        }
        try {
            return errorCodeEntity.getErrorMessage();
        } catch (IllegalArgumentException e) {
            log.error("There is an error extracting error code message for {}",
                    errorCodeEntity.getErrorEnum());
            throw new BusinessException(UNDEFINED_ERROR_CODE, COMMON.name());
        }
    }

    public static String getConfigValue(CommonEnum commonEnum, ConfigurationRepository configurationRepository, String flow) {
        return configurationRepository.findByConfigCode(commonEnum.name())
                .map(ConfigurationEntity::getConfigValue)
                .orElseGet(() -> {
                    log.error("There is no config value for {}", OTP_EXPIRATION_TIME.name());
                    throw new BusinessException(CONFIG_NOT_FOUND, flow); // default value of 5 minutes
                });
    }

    public static String getConfigValue(String key, ConfigurationRepository configurationRepository, String defaultValue) {
        return configurationRepository.findByConfigCode(key)
                .map(ConfigurationEntity::getConfigValue)
                .orElseGet(() -> {
                    log.error("There is no config value for {} ---> Getting default value!", OTP_EXPIRATION_TIME.name());
                    return defaultValue;
                });
    }
}
