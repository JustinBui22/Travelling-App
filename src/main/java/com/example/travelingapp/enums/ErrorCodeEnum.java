package com.example.travelingapp.enums;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import static com.example.travelingapp.enums.CommonEnum.Register;
import static com.example.travelingapp.enums.CommonEnum.Common;

@Log4j2
@Getter
public enum ErrorCodeEnum {
    USER_CREATED("E000", "User created", Register, HttpStatusCodeEnum.OK),
    INVALID_INPUT("E001", "Invalid input provided", Register, HttpStatusCodeEnum.INVALID_INPUT),
    USERNAME_TAKEN("E002", "Username taken", Register, HttpStatusCodeEnum.USERNAME_TAKEN),
    EMAIL_TAKEN("E003", "Email taken", Register, HttpStatusCodeEnum.EMAIL_TAKEN),
    PASSWORD_NOT_QUALIFIED("E004", "Password not qualified", Register, HttpStatusCodeEnum.PASSWORD_NOT_QUALIFIED),
    USER_NOT_FOUND("E005", "User not found", Register, HttpStatusCodeEnum.USER_NOT_FOUND),
    CLIENT_SERVER_ERROR("E006", "Client internal server error", Register, HttpStatusCodeEnum.CLIENT_SERVER_ERROR),
    PASSWORD_NOT_CORRECT("E007", "Password not correct", Register, HttpStatusCodeEnum.PASSWORD_NOT_CORRECT),
    UNDEFINED_ERROR_CODE("E008", "Undefined error code", Register, HttpStatusCodeEnum.UNDEFINED_ERROR_CODE),
    UNDEFINED_HTTP_CODE("E009", "Undefined http status code", Register, HttpStatusCodeEnum.UNDEFINED_HTTP_CODE),
    EMAIL_PATTERN_INVALID("E010", "Email form is invalid", Register, HttpStatusCodeEnum.EMAIL_PATTERN_INVALID),
    PHONE_FORMAT_INVALID("E011", "Phone format is invalid", Register, HttpStatusCodeEnum.PHONE_FORMAT_INVALID),
    SMS_NOT_CONFIG("E012", "Sms config is not found", Common, HttpStatusCodeEnum.CONFIG_NOT_FOUND),
    USERNAME_FORMAT_INVALID("E013", "Username format invalid", Register, HttpStatusCodeEnum.USERNAME_FORMAT_INVALID),;

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

    public static HttpStatusCodeEnum getHttpFromErrorCode(String errorCode) {
        for (ErrorCodeEnum errorStatusCode : ErrorCodeEnum.values()) {
            if (String.valueOf(errorStatusCode.getCode()).equals(errorCode)) {
                // Http code not config with error code OR not exist yet
                if (errorStatusCode.httpStatusCodeEnum == null || errorStatusCode.httpStatusCodeEnum == HttpStatusCodeEnum.UNDEFINED_HTTP_CODE) {
                    log.info("Http code {} correspond with error code {} has not been defined yet!", errorStatusCode.httpStatusCodeEnum, errorCode);
                    return HttpStatusCodeEnum.UNDEFINED_HTTP_CODE;
                }
                return errorStatusCode.httpStatusCodeEnum;
            }
        }
        log.info("Error code {} has not been defined yet!", errorCode);
        return HttpStatusCodeEnum.UNDEFINED_ERROR_CODE;
    }

}
