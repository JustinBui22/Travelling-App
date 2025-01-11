package com.example.travelingapp.service;


public interface EmailService {

    void sendEmail(String senderEmail, String receiverEmail, String emailSubject, String emailContent);
}
