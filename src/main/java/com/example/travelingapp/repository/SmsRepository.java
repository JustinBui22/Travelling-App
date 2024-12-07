package com.example.travelingapp.repository;

import com.example.travelingapp.entity.Sms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmsRepository extends JpaRepository<Sms, Long> {
    Optional<Sms> findBySmsCodeAndSmsFlow (String smsCode, String smsFlow);
}
