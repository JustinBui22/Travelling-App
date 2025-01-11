package com.example.travelingapp.service;

import com.example.travelingapp.response_template.ResponseBody;

public interface SmsService {

    ResponseBody<String> sendSms(String userPhoneNumber, String sms);
}
