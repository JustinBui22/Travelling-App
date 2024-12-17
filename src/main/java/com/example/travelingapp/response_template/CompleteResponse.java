package com.example.travelingapp.response_template;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.enums.HttpStatusCodeEnum;
import com.example.travelingapp.repository.ErrorCodeRepository;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


import java.util.List;
import java.util.Optional;

import static com.example.travelingapp.enums.ErrorCodeEnum.CONFIG_NOT_FOUND;
import static com.example.travelingapp.util.Common.*;

@Log4j2
@Setter
@Getter
public final class CompleteResponse<T> {
    private ResponseBody<T> responseBody;
    private int httpCode;

    public CompleteResponse(ResponseBody<T> responseBody, int httpCode) {
        this.responseBody = responseBody;
        this.httpCode = httpCode;
    }

    public static CompleteResponse<Object> getCompleteResponse(ErrorCodeRepository errorCodeRepository, ErrorCodeEnum errorCodeEnum, String flow, Object object) {
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByErrorEnumAndFlow(errorCodeEnum.name(), flow);
        if (errorCodeOptional.isEmpty()) {
            log.info("There is no error code {} in flow {}, removing flow.", errorCodeEnum.name(), flow);
            errorCodeOptional = errorCodeRepository.findFirstByErrorEnum(errorCodeEnum.name());
            if (errorCodeOptional.isEmpty()) {
                log.info("There is no error code {}", errorCodeEnum.name());
                return new CompleteResponse<>(new ResponseBody<>(CONFIG_NOT_FOUND.getCode(), CONFIG_NOT_FOUND.getMessage(), CONFIG_NOT_FOUND.getFlow().name(), null), CONFIG_NOT_FOUND.getHttpStatusCodeEnum().value);
            }
        }
        String errorCode = getErrorCode(errorCodeOptional.get());
        String errorMessage = getErrorCodeMessage(errorCodeOptional.get());
        String errorDescription = errorCodeOptional.map(ErrorCode::getErrorDescription).orElse(null);
        HttpStatusCodeEnum httpStatusCode = getHttpFromErrorCode(errorCodeOptional.get());
        if (object == null) {
            return new CompleteResponse<>(new ResponseBody<>(errorCode, errorMessage, flow, errorDescription), httpStatusCode.value);
        }
        return new CompleteResponse<>(new ResponseBody<>(errorCode, errorMessage, flow, object), httpStatusCode.value());
    }

}



