package com.example.travelingapp.Validator;

import com.example.travelingapp.entity.Configuration;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.regex.Pattern;


@Log4j2
public class Validator {
    private static boolean validateInput(String input, Optional<Configuration> configEntity, String defaultPattern, String logMessage) {
        String patternString = configEntity.map(Configuration::getConfigValue).orElseGet(() -> {
            log.info(logMessage);
            return defaultPattern;
        });

        Pattern pattern = Pattern.compile(patternString);
        return input != null && pattern.matcher(input).matches();
    }

    public static boolean validateEmailForm(String email, Optional<Configuration> emailConfigEntity) {
        String backUpEmailPattern = "^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String logMessage = "Config for email pattern not found";
        return validateInput(email, emailConfigEntity, backUpEmailPattern, logMessage);
    }

    public static boolean validatePassword(String password, Optional<Configuration> passwordConfigEntity) {
        String backUpPasswordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>?])[^\\s<>\\\\/]{8,20}$";
        String logMessage = "Config for password pattern not found";
        return validateInput(password, passwordConfigEntity, backUpPasswordPattern, logMessage);
    }

    public static boolean validatePhoneForm(String phone, Optional<Configuration> phoneConfigEntity) {
        String backUpPhonePattern = "^(0|84|\\+84)(3|5|7|8|9)\\d{7,8}$";
        String logMessage = "Config for phone number pattern in Vietnam not found";
        return validateInput(phone, phoneConfigEntity, backUpPhonePattern, logMessage);
    }

    public static boolean validateUsername(String username, Optional<Configuration> usernameConfigEntity) {
        String backUpUsernamePattern = "^(?!^(0|84|\\+84)(3|5|7|8|9)\\d{7,8}$)(?!.*[\\u0008\\t])[a-zA-Z0-9_.-]{5,20}$";
        String logMessage = "Config for phone number pattern in Vietnam not found";
        return validateInput(username, usernameConfigEntity, backUpUsernamePattern, logMessage);
    }
}
