package com.example.travelingapp.enums;

import lombok.Getter;

import static com.example.travelingapp.enums.CommonEnum.*;

@Getter
public enum ErrorCodeEnum {
    USER_CREATED("E000", "User created", Register, HttpStatusCodeEnum.USER_CREATED),
    INVALID_INPUT("E001", "Invalid input provided", Register, HttpStatusCodeEnum.INVALID_INPUT),
    USERNAME_TAKEN("E002", "Username taken", Register, HttpStatusCodeEnum.USERNAME_TAKEN),
    EMAIL_TAKEN("E003", "Email taken", Register, HttpStatusCodeEnum.EMAIL_TAKEN),
    PASSWORD_NOT_QUALIFIED("E004", "Password not qualified", Register, HttpStatusCodeEnum.PASSWORD_NOT_QUALIFIED),
    CLIENT_SERVER_ERROR("E005", "Client internal server error", Register, HttpStatusCodeEnum.CLIENT_SERVER_ERROR),
    USERNAME_PASSWORD_NOT_CORRECT("E006", "Username or Password is not correct", Login, HttpStatusCodeEnum.USERNAME_PASSWORD_NOT_CORRECT),
    UNDEFINED_ERROR_CODE("E007", "Undefined error code", Register, HttpStatusCodeEnum.UNDEFINED_ERROR_CODE),
    UNDEFINED_HTTP_CODE("E008", "Undefined http status code", Register, HttpStatusCodeEnum.UNDEFINED_HTTP_CODE),
    EMAIL_PATTERN_INVALID("E009", "Email form is invalid", Register, HttpStatusCodeEnum.EMAIL_PATTERN_INVALID),
    PHONE_FORMAT_INVALID("E010", "Phone format is invalid", Register, HttpStatusCodeEnum.PHONE_FORMAT_INVALID),
    SMS_NOT_CONFIG("E011", "Sms config is not found", Common, HttpStatusCodeEnum.CONFIG_NOT_FOUND),
    LOGIN_SUCCESS("E012", "Log in successfully", Login, HttpStatusCodeEnum.LOGIN_SUCCESS);

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

    public static HttpStatusCodeEnum.Series getHttpFromErrorCode(String errorCode) {
        for (ErrorCodeEnum errorStatusCode : ErrorCodeEnum.values()) {
            if (String.valueOf(errorStatusCode.getCode()).equals(errorCode)) {
                return errorStatusCode.httpStatusCodeEnum.series();
            }
        }
        return null;
    }

}
