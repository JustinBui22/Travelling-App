package com.example.travelingapp.util.common;

import com.example.travelingapp.entity.Configuration;
import com.example.travelingapp.repository.ConfigurationRepository;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.NON_AUTHENTICATED_REQUEST;

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
            // Log the warning if no configuration is found
            log.warn("There is no config for {}", NON_AUTHENTICATED_REQUEST);
            // Return an empty array if no configuration is found
            return new String[0];
        }
    }
}
