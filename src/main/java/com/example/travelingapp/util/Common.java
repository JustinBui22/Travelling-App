package com.example.travelingapp.util;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.enums.HttpStatusCodeEnum;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ConfigurationRepository;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.Common;
import static com.example.travelingapp.enums.CommonEnum.NON_AUTHENTICATED_REQUEST;
import static com.example.travelingapp.enums.ErrorCodeEnum.UNDEFINED_ERROR_CODE;
import static com.example.travelingapp.enums.ErrorCodeEnum.UNDEFINED_HTTP_CODE;

@Log4j2
public class Common {

    public static String[] getNonAuthenticatedUrls(ConfigurationRepository configurationRepository) {
        Optional<Configuration> nonAuthenRequestUrlOptional = configurationRepository.findByConfigCode(NON_AUTHENTICATED_REQUEST.name());
        if (nonAuthenRequestUrlOptional.isPresent()) {
            // Clean and split the configuration value to get individual URLs
            String[] nonAuthenticatedUrls = nonAuthenRequestUrlOptional.get().getConfigValue()
                    .replaceAll("[{}]", "") // Remove curly braces

                    .split(","); // Split by commas to get individual URLs
            // Clean up each URL in the array (remove newlines, trim spaces)
            return Arrays.stream(nonAuthenticatedUrls)
                    .map(url -> url.replaceAll("\\n", "").trim()) // Remove newline characters and trim spaces
                    .toArray(String[]::new);
        } else {
            log.warn("There is no config for {}", NON_AUTHENTICATED_REQUEST);
            return new String[0];
        }
    }

    public static HttpStatusCodeEnum getHttpFromErrorCode(ErrorCode errorCode) {
        if (errorCode == null) {
            log.info("Error code is null, returning undefined HTTP status code.");
            return UNDEFINED_HTTP_CODE.getHttpStatusCodeEnum();
        }
        try {
            // Map the HTTP code using the ErrorCode object
            return HttpStatusCodeEnum.valueOf(errorCode.getHttpCode());
        } catch (IllegalArgumentException e) {
            // Log and return default if mapping fails
            log.error("There is an error extracting http code for {}",
                    errorCode.getErrorEnum());
            throw new BusinessException(UNDEFINED_HTTP_CODE, Common.name());
        }
    }

    public static String getErrorCode(ErrorCode errorCode) {
        if (errorCode == null) {
            log.info("Error code is null, returning undefined error code.");
            return UNDEFINED_ERROR_CODE.getCode();
        }
        try {
            return errorCode.getErrorCode();
        } catch (IllegalArgumentException e) {
            log.error("There is an error extracting error code for {}!",
                    errorCode.getErrorEnum());
            throw new BusinessException(UNDEFINED_ERROR_CODE, Common.name());
        }
    }

    public static String getErrorCodeMessage(ErrorCode errorCode) {
        if (errorCode == null) {
            log.info("Error code message is null, returning undefined error code message.");
            return UNDEFINED_ERROR_CODE.getMessage();
        }
        try {
            return errorCode.getErrorMessage();
        } catch (IllegalArgumentException e) {
            log.error("There is an error extracting error code message for {}",
                    errorCode.getErrorEnum());
            throw new BusinessException(UNDEFINED_ERROR_CODE, Common.name());
        }
    }
}
