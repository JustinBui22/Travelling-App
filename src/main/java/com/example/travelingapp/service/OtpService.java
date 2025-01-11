package com.example.travelingapp.service;

import com.example.travelingapp.dto.OtpDTO;
import com.example.travelingapp.response_template.CompleteResponse;

public interface OtpService {
    CompleteResponse<Object> sendOtp(OtpDTO otpDTO);

    CompleteResponse<Object> generateVerificationOtp();

    CompleteResponse<Object> verifyOtp(OtpDTO otpDTO);
}
