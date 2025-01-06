package com.example.travelingapp.controller;

import com.example.travelingapp.response_template.ResponseBody;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/private/auth/token/")
public interface TokenController {
    @GetMapping("/generate")
    ResponseEntity<ResponseBody<Object>> generateToken(@NotNull @RequestParam(value = "userName") String userName);

    @GetMapping("/refresh")
    ResponseEntity<ResponseBody<Object>> refreshToken(@NotNull @RequestHeader("Authorization") String authorizationHeader,
                                                      @NotNull @RequestHeader(value = "Session-Token") String sessionTokenHeader,
                                                      @NotNull @RequestParam(value = "userName") String userName);
}
