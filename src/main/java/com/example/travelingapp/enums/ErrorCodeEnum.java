package com.example.travelingapp.enums;

import lombok.Getter;

import static com.example.travelingapp.enums.Enum.Register;

@Getter
public enum ErrorCodeEnum {

    INVALID_INPUT("E001", "Invalid input provided", Register, HttpStatusCodeEnum.INVALID_INPUT),
    USERNAME_TAKEN("E002", "Username taken", Register, HttpStatusCodeEnum.USERNAME_TAKEN),
    EMAIL_TAKEN("E003", "Email taken", Register, HttpStatusCodeEnum.EMAIL_TAKEN),
    EMAIL_PATTERN_INVALID("E010", "Email form is invalid", Register, HttpStatusCodeEnum.EMAIL_PATTERN_INVALID),
    PHONE_FORMAT_INVALID("E011", "Phone format is invalid", Register, HttpStatusCodeEnum.PHONE_FORMAT_INVALID),
    PASSWORD_NOT_QUALIFIED("E004", "Password not qualified", Register, HttpStatusCodeEnum.PASSWORD_NOT_QUALIFIED),
    USER_NOT_FOUND("E005", "User not found", Register, HttpStatusCodeEnum.USER_NOT_FOUND),
    CLIENT_SERVER_ERROR("E006", "Client internal server error", Register, HttpStatusCodeEnum.CLIENT_SERVER_ERROR),
    PASSWORD_NOT_CORRECT("E007", "Password not correct", Register, HttpStatusCodeEnum.PASSWORD_NOT_CORRECT),
    UNDEFINED_ERROR_CODE("E008", "Undefined error code", Register, HttpStatusCodeEnum.UNDEFINED_ERROR_CODE),
    UNDEFINED_HTTP_CODE("E009", "Undefined http status code", Register, HttpStatusCodeEnum.UNDEFINED_HTTP_CODE),
    USER_CREATED("E000", "User created", Register, HttpStatusCodeEnum.USER_CREATED);

    private final String code;
    private final String message;
    private final Enum flow;
    private final HttpStatusCodeEnum httpStatusCodeEnum;

    ErrorCodeEnum(String code, String message, Enum flow, HttpStatusCodeEnum httpStatusCodeEnum) {
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
