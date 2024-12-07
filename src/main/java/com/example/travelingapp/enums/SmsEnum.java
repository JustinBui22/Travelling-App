package com.example.travelingapp.enums;

import static com.example.travelingapp.enums.CommonEnum.Register;
import lombok.Getter;

@Getter
public enum SmsEnum {
    SMS_OTP_REGISTER("SMS01", "Sms send otp to register through phone number", Register);

    private final String code;
    private final String description;
    private final CommonEnum flow;

    SmsEnum(String code, String description, CommonEnum flow) {
        this.code = code;
        this.description = description;
        this.flow = flow;
    }
}
