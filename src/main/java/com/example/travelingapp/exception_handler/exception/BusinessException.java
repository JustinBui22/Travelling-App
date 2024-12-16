package com.example.travelingapp.exception_handler.exception;

import com.example.travelingapp.enums.ErrorCodeEnum;
import lombok.Getter;

import java.io.Serial;

@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ErrorCodeEnum errorCodeEnum;
    private final String flow;

    public BusinessException(ErrorCodeEnum errorCodeEnum, String flow) {
        this.errorCodeEnum = errorCodeEnum;
        this.flow = flow;
    }
}
