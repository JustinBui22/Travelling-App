package com.example.travelingapp.service.impl;

import com.example.travelingapp.service.SmsService;
import com.example.travelingapp.response_template.ResponseBody;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public ResponseBody<String> sendSms(String userPhoneNumber, String Sms) {

        return null;
    }
}
