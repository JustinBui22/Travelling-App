package com.example.travelingapp.util;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public final class ResponseBody<T> {
    // Getters and setters
    private String code;
    private String message;
    private String flow;
    private T body;

    public ResponseBody(String code, String message, String flow, T body) {
        this.code = code;
        this.message = message;
        this.flow = flow;
        this.body = body;
    }

    public ResponseBody(String code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }
}
