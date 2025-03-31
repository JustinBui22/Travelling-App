package com.example.travelingapp.exception_handler;

import com.example.travelingapp.entity.ErrorCode;
import com.example.travelingapp.repository.ErrorCodeRepository;
import com.example.travelingapp.util.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Objects;
import java.util.Optional;

import static com.example.travelingapp.enums.ErrorCodeEnum.INVALID_INPUT;
import static com.example.travelingapp.enums.ErrorCodeEnum.UNDEFINED_ERROR_CODE;

@RestControllerAdvice
public class MethodValidationExceptionHandler {

    private final ErrorCodeRepository errorCodeRepository;

    public MethodValidationExceptionHandler(ErrorCodeRepository errorCodeRepository) {
        this.errorCodeRepository = errorCodeRepository;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBody<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage;
        if (ex.getBindingResult().hasErrors()) {
            errorMessage = new StringBuilder(Objects.requireNonNull(ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage())).append(";");
            Optional<ErrorCode> errorCodeOptional = errorCodeRepository.findByErrorEnum(String.valueOf(INVALID_INPUT));
            String errorCode = errorCodeOptional.map(ErrorCode::getErrorCode)
                    .orElse(INVALID_INPUT.getCode().isEmpty() ? UNDEFINED_ERROR_CODE.getCode() : INVALID_INPUT.getCode());

            String errorCodeMessage = errorCodeOptional.map(ErrorCode::getErrorMessage)
                    .orElse(INVALID_INPUT.getMessage().isEmpty() ? UNDEFINED_ERROR_CODE.getMessage() : INVALID_INPUT.getMessage());

            ResponseBody<String> response = new ResponseBody<>
                    (errorCode, errorCodeMessage, errorMessage.toString());
            return ResponseEntity.badRequest().body(response);
        }
        return null;
    }
}

//                errorMessage = new StringBuilder();
//                ex.getBindingResult().getAllErrors().forEach(error -> {
//                    String fieldName = ((FieldError) error).getField();
//                    String message = error.getDefaultMessage();
//                    errorMessage.append(fieldName).append(": ").append(message).append("; ");
//                });
