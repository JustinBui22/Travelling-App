package com.example.travelingapp.enums;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

import static com.example.travelingapp.enums.CommonEnum.*;

@Log4j2
@Getter
public enum ErrorCodeEnum {
    USER_CREATED("E000", "User created", Register, HttpStatusCodeEnum.OK),
    LOGIN_SUCCESS("E000", "Log in successfully", Login, HttpStatusCodeEnum.OK),
    TOKEN_GENERATE_SUCCESS("E000", "Token generate successfully", Token, HttpStatusCodeEnum.OK),
    TOKEN_VERIFY_SUCCESS("E000", "Token verified successfully", Token, HttpStatusCodeEnum.OK),
    INVALID_INPUT("E001", "Invalid input provided", Register, HttpStatusCodeEnum.INVALID_INPUT),
    USERNAME_TAKEN("E002", "Username taken", Register, HttpStatusCodeEnum.USERNAME_TAKEN),
    EMAIL_TAKEN("E003", "Email taken", Register, HttpStatusCodeEnum.EMAIL_TAKEN),
    PASSWORD_NOT_QUALIFIED("E004", "Password not qualified", Register, HttpStatusCodeEnum.PASSWORD_NOT_QUALIFIED),
    USER_NOT_FOUND("E005", "User not found", Login, HttpStatusCodeEnum.USER_NOT_FOUND),
    CLIENT_SERVER_ERROR("E006", "Client internal server error", Register, HttpStatusCodeEnum.CLIENT_SERVER_ERROR),
    PASSWORD_NOT_CORRECT("E007", "Password not correct", Login, HttpStatusCodeEnum.PASSWORD_NOT_CORRECT),
    UNDEFINED_ERROR_CODE("E008", "Undefined error code", Register, HttpStatusCodeEnum.UNDEFINED_ERROR_CODE),
    UNDEFINED_HTTP_CODE("E009", "Undefined http status code", Register, HttpStatusCodeEnum.UNDEFINED_HTTP_CODE),
    EMAIL_PATTERN_INVALID("E010", "Email form is invalid", Register, HttpStatusCodeEnum.EMAIL_PATTERN_INVALID),
    PHONE_FORMAT_INVALID("E011", "Phone format is invalid", Register, HttpStatusCodeEnum.PHONE_FORMAT_INVALID),
    SMS_NOT_CONFIG("E012", "Sms config is not found", Common, HttpStatusCodeEnum.CONFIG_NOT_FOUND),
    USERNAME_FORMAT_INVALID("E013", "Username format invalid", Register, HttpStatusCodeEnum.USERNAME_FORMAT_INVALID),
    TOKEN_GENERATE_FAIL("E014", "Token generate fail", Token, HttpStatusCodeEnum.TOKEN_GENERATE_FAIL),
    TOKEN_VERIFY_FAIL("E015", "Token verify fail", Token, HttpStatusCodeEnum.TOKEN_VERIFY_FAIL),
    TOKEN_EXPIRE("E016", "Token expires", Token, HttpStatusCodeEnum.TOKEN_EXPIRE),
    INTERNAL_SERVER_ERROR("E017", "Internal server error", Common, HttpStatusCodeEnum.INTERNAL_SERVER_ERROR),;


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
            log.error("Http code {} correspond with error code {} has not been defined yet!",
                    errorCode.getHttpCode(), errorCode.getErrorEnum());
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
            log.error("There is no config value of error code for {}!",
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
            log.error("There is no config value of error code message for {}",
                    errorCode.getErrorEnum());
            throw new BusinessException(UNDEFINED_ERROR_CODE, Common.name());
        }
    }
}
