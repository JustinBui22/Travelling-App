package com.example.travelingapp.service.impl;

import com.example.travelingapp.service.SmsService;
import com.example.travelingapp.response_template.ResponseBody;
import org.springframework.stereotype.Service;

import static com.example.travelingapp.enums.CommonEnum.SMS;
import static com.example.travelingapp.enums.ErrorCodeEnum.SMS_SENT_SUCCESS;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public ResponseBody<String> sendSms(String userPhoneNumber, String sms) {

        return new ResponseBody<>(SMS_SENT_SUCCESS.getCode(), null, SMS.name(), null);
    }
}
