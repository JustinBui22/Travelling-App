package com.example.travelingapp.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_INPUT("E001", "Invalid input provided", HttpStatusCode.BAD_REQUEST),
    USERNAME_TAKEN("E002", "Username taken", HttpStatusCode.BAD_REQUEST),
    EMAIL_TAKEN("E003", "Email taken", HttpStatusCode.BAD_REQUEST),
    PASSWORD_NOT_QUALIFIED("E004", "Password not qualified", HttpStatusCode.BAD_REQUEST),
    USER_NOT_FOUND("E005", "User not found", HttpStatusCode.NOT_FOUND),
    SERVER_ERROR("E006", "Internal server error", HttpStatusCode.INTERNAL_SERVER_ERROR),
    PASSWORD_NOT_CORRECT("E007", "Password not correct", HttpStatusCode.BAD_REQUEST),
    UNDEFINED_ERROR_CODE("E008", "Undefined error code", HttpStatusCode.BAD_REQUEST),

    USER_CREATED("E009", "User created", HttpStatusCode.OK);

    private final String code;
    private final String message;
    @Getter
    private final HttpStatusCode httpStatusCode;

    ErrorCode(String code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

}
