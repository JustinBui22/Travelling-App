package com.example.travelingapp.util;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.enums.HttpStatusCodeEnum;
import com.example.travelingapp.repository.ErrorCodeRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

import static com.example.travelingapp.enums.ErrorCodeEnum.UNDEFINED_ERROR_CODE;
import static com.example.travelingapp.enums.ErrorCodeEnum.getHttpFromErrorCode;

@Setter
@Getter
public final class CompleteResponse<T> {
    private ResponseBody<T> responseBody;
    private int httpCode;

    public CompleteResponse(ResponseBody<T> responseBody, int httpCode) {
        this.responseBody = responseBody;
        this.httpCode = httpCode;
    }

    public static CompleteResponse<Object> getCompleteResponse(ErrorCodeRepository errorCodeRepository, String errorCode, String flow) {
        HttpStatusCodeEnum httpStatusCode = getHttpFromErrorCode(errorCode);
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByErrorCodeAndFlow(errorCode, flow);
        String errorMessage = errorCodeOptional.map(ErrorCode::getErrorMessage).orElse(UNDEFINED_ERROR_CODE.getMessage());
        String errorDescription = errorCodeOptional.map(ErrorCode::getErrorDescription).orElse(null);
        return new CompleteResponse<>(new ResponseBody<>(errorCode, errorMessage, flow, errorDescription), httpStatusCode.value());
    }

    public static CompleteResponse<Object> getCompleteResponse(ErrorCodeRepository errorCodeRepository, String errorCode, String errorEnum, String flow, String token) {
        HttpStatusCodeEnum httpStatusCode = getHttpFromErrorCode(errorCode);
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByErrorCodeAndErrorEnumAndFlow(errorCode, errorEnum, flow);
        String errorMessage = errorCodeOptional.map(ErrorCode::getErrorMessage).orElse(UNDEFINED_ERROR_CODE.getMessage());
        return new CompleteResponse<>(new ResponseBody<>(errorCode, errorMessage, flow, token), httpStatusCode.value());
    }
}



