package com.example.travelingapp.service;

import com.example.travelingapp.dto.UserDTO;
import com.example.travelingapp.util.ResponseBody;

public interface SmsService {

    ResponseBody<String> sendSms(String userPhoneNumber, String Sms);
}
