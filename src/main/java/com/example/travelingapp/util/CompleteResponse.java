package com.example.travelingapp.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Setter
@Getter
public final class CompleteResponse<T> {
    private ResponseBody<T> responseBody;
    private int httpCode;

    public CompleteResponse(ResponseBody<T> responseBody, int httpCode) {
        this.responseBody = responseBody;
        this.httpCode = httpCode;
    }

    public CompleteResponse() {
    }

    public ResponseEntity<Object> createResponse(ResponseBody<T> responseBody, int httpCode) {
        CompleteResponse<T> responseData = new CompleteResponse<>();
        responseData.setResponseBody(responseBody);
        responseData.setHttpCode(httpCode);
        return new ResponseEntity<>(responseData, HttpStatusCode.valueOf(responseData.getHttpCode()));
    }
}



