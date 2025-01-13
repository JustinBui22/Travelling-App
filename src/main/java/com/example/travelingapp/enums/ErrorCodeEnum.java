package com.example.travelingapp.enums;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;


import static com.example.travelingapp.enums.CommonEnum.*;

@Log4j2
@Getter
public enum ErrorCodeEnum {
    USER_CREATED("E000", "User created", REGISTER, HttpStatusCodeEnum.OK),
    LOGIN_SUCCESS("E000", "Log in successfully", LOGIN, HttpStatusCodeEnum.OK),
    TOKEN_GENERATE_SUCCESS("E000", "Token generate successfully", TOKEN, HttpStatusCodeEnum.OK),
    TOKEN_RETRIEVE_SUCCESS("E000", "Token retrieved successfully", TOKEN, HttpStatusCodeEnum.OK),
    TOKEN_VERIFY_SUCCESS("E000", "Token verified successfully", TOKEN, HttpStatusCodeEnum.OK),
    OTP_VERIFICATION_SUCCESS("E000", "OTP code verification successfully", OTP, HttpStatusCodeEnum.OK),
    OTP_CREATED_SUCCESS("E000","Otp created successfully", OTP, HttpStatusCodeEnum.OK),
    OTP_SENT_SUCCESS("E000","Otp sent successfully", OTP, HttpStatusCodeEnum.OK),
    SMS_SENT_SUCCESS("E000", "Sms sent successfully", SMS, HttpStatusCodeEnum.OK),
    EMAIL_SENT_SUCCESS("E000", "Email sent successfully", EMAIL, HttpStatusCodeEnum.OK),
    INVALID_INPUT("E001", "Invalid input provided", REGISTER, HttpStatusCodeEnum.INVALID_INPUT),
    USERNAME_TAKEN("E002", "Username taken", REGISTER, HttpStatusCodeEnum.USERNAME_TAKEN),
    EMAIL_TAKEN("E003", "Email taken", REGISTER, HttpStatusCodeEnum.EMAIL_TAKEN),
    PASSWORD_NOT_QUALIFIED("E004", "Password not qualified", REGISTER, HttpStatusCodeEnum.PASSWORD_NOT_QUALIFIED),
    USER_NOT_FOUND("E005", "User not found", LOGIN, HttpStatusCodeEnum.USER_NOT_FOUND),
    CLIENT_SERVER_ERROR("E006", "Client internal server error", REGISTER, HttpStatusCodeEnum.CLIENT_SERVER_ERROR),
    PASSWORD_NOT_CORRECT("E007", "Password not correct", LOGIN, HttpStatusCodeEnum.PASSWORD_NOT_CORRECT),
    UNDEFINED_ERROR_CODE("E008", "Undefined error code", REGISTER, HttpStatusCodeEnum.UNDEFINED_ERROR_CODE),
    UNDEFINED_HTTP_CODE("E009", "Undefined http status code", REGISTER, HttpStatusCodeEnum.UNDEFINED_HTTP_CODE),
    EMAIL_PATTERN_INVALID("E010", "Email form is invalid", REGISTER, HttpStatusCodeEnum.EMAIL_PATTERN_INVALID),
    PHONE_FORMAT_INVALID("E011", "Phone format is invalid", REGISTER, HttpStatusCodeEnum.PHONE_FORMAT_INVALID),
    SMS_NOT_CONFIG("E012", "Sms config is not found", COMMON, HttpStatusCodeEnum.CONFIG_NOT_FOUND),
    USERNAME_FORMAT_INVALID("E013", "Username format invalid", REGISTER, HttpStatusCodeEnum.USERNAME_FORMAT_INVALID),
    TOKEN_GENERATE_FAIL("E014", "Token generate fail", TOKEN, HttpStatusCodeEnum.TOKEN_GENERATE_FAIL),
    TOKEN_VERIFY_FAIL("E015", "Token verify fail", TOKEN, HttpStatusCodeEnum.TOKEN_VERIFY_FAIL),
    TOKEN_EXPIRE("E016", "Token expires", TOKEN, HttpStatusCodeEnum.TOKEN_EXPIRE),
    INTERNAL_SERVER_ERROR("E017", "Internal server error", COMMON, HttpStatusCodeEnum.INTERNAL_SERVER_ERROR),
    CONFIG_NOT_FOUND("E018", "Config not found", COMMON, HttpStatusCodeEnum.CONFIG_NOT_FOUND),
    INPUT_FORMAT_INVALID("E019", "Input format invalid", COMMON, HttpStatusCodeEnum.INPUT_FORMAT_INVALID),
    OTP_VERIFICATION_FAIL("E020", "OTP code verification fail", COMMON, HttpStatusCodeEnum.OTP_VERIFICATION_FAIL),
    TOKEN_NOT_FOUND("E021", "Token not found", TOKEN, HttpStatusCodeEnum.OK),
    MAX_SESSIONS_REACHED("E022","Max session reached", LOGIN, HttpStatusCodeEnum.MAX_SESSIONS_REACHED),
    SESSION_TOKEN_INVALID("E023","Max session reached", LOGIN, HttpStatusCodeEnum.MAX_SESSIONS_REACHED),
    SMS_SENT_FAIL("E024", "Sms sent failed", SMS, HttpStatusCodeEnum.SMS_SENT_FAIL),
    EMAIL_SENT_FAIL("E025", "Email sent failed", SMS, HttpStatusCodeEnum.EMAIL_SENT_FAIL),
    MAX_OTP_RETRY("E026", "Max OTP retry exceeded", OTP, HttpStatusCodeEnum.MAX_OTP_RETRY),
    VERIFICATION_OTP_EXPIRED("E027", "Verification OTP expired", OTP, HttpStatusCodeEnum.VERIFICATION_OTP_EXPIRED),
    ;


    private final String code;
    private final String message;
    private final CommonEnum flow;
    private final HttpStatusCodeEnum httpStatusCodeEnum;

    ErrorCodeEnum(String code, String message, CommonEnum flow, HttpStatusCodeEnum httpStatusCodeEnum) {
        this.code = code;
        this.message = message;
        this.flow = flow;
        this.httpStatusCodeEnum = httpStatusCodeEnum;
    }
}
