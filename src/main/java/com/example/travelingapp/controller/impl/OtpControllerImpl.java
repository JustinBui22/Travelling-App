package com.example.travelingapp.controller.impl;

import com.example.travelingapp.controller.OtpController;
import com.example.travelingapp.dto.OtpDTO;
import com.example.travelingapp.response_template.CompleteResponse;
import com.example.travelingapp.response_template.ResponseBody;
import com.example.travelingapp.service.OtpService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtpControllerImpl implements OtpController {
    private final OtpService otpService;

    public OtpControllerImpl(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public ResponseEntity<ResponseBody<Object>> sendOtp(OtpDTO otpDTO) {
        CompleteResponse<Object> response = otpService.sendOtp(otpDTO);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));
    }

    @Override
    public ResponseEntity<ResponseBody<Object>> verifyOtp(OtpDTO otpDTO) {
        CompleteResponse<Object> response = otpService.verifyOtp(otpDTO);
        return new ResponseEntity<>(response.getResponseBody(), HttpStatusCode.valueOf(response.getHttpCode()));
    }
}
