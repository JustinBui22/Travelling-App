package com.example.travelingapp.exception_handler;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.exception_handler.exception.BusinessException;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.response_template.CompleteResponse;
import com.example.travelingapp.response_template.ResponseBody;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static com.example.travelingapp.enums.CommonEnum.Common;
import static com.example.travelingapp.enums.ErrorCodeEnum.*;
import static com.example.travelingapp.response_template.CompleteResponse.getCompleteResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ErrorCodeRepository errorCodeRepository;

    public GlobalExceptionHandler(ErrorCodeRepository errorCodeRepository) {
        this.errorCodeRepository = errorCodeRepository;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseBody<Object>> handleBusinessExceptions(BusinessException ex, String flow) {
        CompleteResponse<Object> result = getCompleteResponse(errorCodeRepository, ex.getErrorCodeEnum(), flow, null);
        return new ResponseEntity<>(result.getResponseBody(), HttpStatusCode.valueOf(result.getHttpCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBody<Object>> handleMethodValidationExceptions(MethodArgumentNotValidException ex) {
        if (ex.getBindingResult().hasErrors()) {
            String errorMessage = Objects.requireNonNull(ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
            CompleteResponse<Object> result = getCompleteResponse(errorCodeRepository, INVALID_INPUT, Common.name(), errorMessage);
            return new ResponseEntity<>(result.getResponseBody(), HttpStatusCode.valueOf(result.getHttpCode()));
        }
        return null;
    }
}
