package com.example.travelingapp.dto;

import com.example.travelingapp.enums.EmailEnum;
import com.example.travelingapp.enums.SmsEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Valid
public class OtpDTO {
    private String email;

    @NotBlank(message = "Username cannot be null or empty")
    private String userName;

    @Size(max = 15, message = "Phone number must be between 9 and 15 characters")
    private String phoneNumber;

    @NotBlank(message = "OTP verification method cannot be null or empty")
    private String otpVerificationMethod;

    private String otp;

    private SmsEnum smsEnum;

    private EmailEnum emailEnum;

    public OtpDTO(String userName, String otp) {
        this.userName = userName;
        this.otp = otp;
    }
}

