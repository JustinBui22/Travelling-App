package com.example.travelingapp.util;

import com.example.travelingapp.entity.Configuration;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.example.travelingapp.enums.Enum.*;
import static com.example.travelingapp.enums.Enum.PHONE_VN_PATTERN;


@Log4j2
public class Validator {

    public static boolean validateEmailForm(String email, Optional<Configuration> emailConfigEntity) {
        String emailPattern;
        if (emailConfigEntity.isEmpty()) {
            log.info("Config for email pattern {} not found", EMAIL_PATTERN.name());
            emailPattern = "^(?=.{1,254}$)(?=.{1,64}@)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        } else {
            emailPattern = emailConfigEntity.get().getConfigValue();
        }
        Pattern pattern = Pattern.compile(String.valueOf(emailPattern));
        return pattern.matcher(email).matches();
    }

    public static boolean validatePassword(String password, Optional<Configuration> passwordConfigEntity) {
        String passwordPattern;
        if (passwordConfigEntity.isEmpty()) {
            log.info("Config for password pattern {} not found", PASSWORD_PATTERN.name());
            passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+\\[\\]{}|;:'\",.<>?])[^\\s<>\\\\/]{8,20}$";
        } else {
            passwordPattern = passwordConfigEntity.get().getConfigValue();
        }
        Pattern pattern = Pattern.compile(passwordPattern);
        return password != null && pattern.matcher(password).matches();
    }

    public static boolean validatePhoneForm(String phone, Optional<Configuration> phoneConfigEntity) {
        String phonePattern;
        if (phoneConfigEntity.isEmpty()) {
            log.info("Config for phone number pattern in Vietnam {} not found", PHONE_VN_PATTERN.name());
            phonePattern = "^(0|84|\\+84)([35789])\\d{7,8}$\n";
        } else {
            phonePattern = phoneConfigEntity.get().getConfigValue();
        }
        Pattern pattern = Pattern.compile(phonePattern);
        return pattern.matcher(phone).matches();
    }
}
