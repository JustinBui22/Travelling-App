package com.example.travelingapp.util.common;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.enums.ErrorCodeEnum;
import com.example.travelingapp.repository.ErrorCodeRepository;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

import static com.example.travelingapp.enums.ErrorCodeEnum.UNDEFINED_ERROR_CODE;

@Log4j2
public class ErrorCodeResolver {
    public static String resolveErrorCode(ErrorCodeRepository errorCodeRepository, ErrorCodeEnum errorCodeEnum) {
        Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByHttpCode(String.valueOf(errorCodeEnum));
        return errorCodeOptional.map(ErrorCode::getErrorCode)
                .orElseGet(() -> {
                    if (errorCodeEnum.getCode().isEmpty()) {
                        log.info("Error code is undefined for {}", errorCodeEnum);
                        return UNDEFINED_ERROR_CODE.getCode();
                    }
                    return errorCodeEnum.getCode();
                });
    }
}
