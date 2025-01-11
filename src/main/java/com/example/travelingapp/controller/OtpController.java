package com.example.travelingapp.controller;

import com.example.travelingapp.dto.OtpDTO;
import com.example.travelingapp.response_template.ResponseBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/public/api/otp/")
public interface OtpController {
    @PostMapping("/send")
    ResponseEntity<ResponseBody<Object>> sendOtp(@Valid @RequestBody OtpDTO otpRequest);

    @PostMapping("/verify")
    ResponseEntity<ResponseBody<Object>> verifyOtp(@Valid @RequestBody OtpDTO otpRequest);
}